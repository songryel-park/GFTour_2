package com.gftour.service

import com.gftour.domain.Booking
import com.gftour.domain.BookingStatus
import com.gftour.domain.TourStatus
import com.gftour.exception.*
import com.gftour.repository.BookingRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 예약 서비스
 * 예약 생성, 수정, 취소 및 조회 관련 비즈니스 로직을 처리
 */
@Service
@Transactional
class BookingService(
    private val bookingRepository: BookingRepository,
    private val userService: UserService,
    private val tourService: TourService
) {
    private val logger = LoggerFactory.getLogger(BookingService::class.java)

    /**
     * 예약 생성
     * 
     * @param userId 사용자 ID
     * @param tourId 투어 ID
     * @param participantCount 참가자 수
     * @param specialRequests 특별 요청사항
     * @return 생성된 예약 정보
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws TourNotFoundException 투어를 찾을 수 없는 경우
     * @throws TourNotAvailableException 예약할 수 없는 투어인 경우
     * @throws DuplicateBookingException 이미 예약된 투어인 경우
     * @throws InvalidParticipantCountException 잘못된 참가자 수인 경우
     * @throws InsufficientCapacityException 투어 정원이 부족한 경우
     */
    fun createBooking(
        userId: Long,
        tourId: Long,
        participantCount: Int,
        specialRequests: String? = null
    ): Booking {
        logger.info("예약 생성 시도: userId={}, tourId={}, participantCount={}", 
                   userId, tourId, participantCount)
        
        val user = userService.getUserById(userId)
        val tour = tourService.getTourById(tourId)
        
        // 사용자 활성 상태 확인
        if (!user.isActive()) {
            throw UserNotActiveException("비활성화된 사용자는 예약할 수 없습니다")
        }
        
        // 예약 생성 전 유효성 검사
        validateBookingCreation(user.id, tour.id, participantCount)
        
        // 투어 예약 가능 여부 확인
        if (!tour.isBookable()) {
            logger.warn("예약 불가능한 투어: tourId={}, status={}", tourId, tour.status)
            throw TourNotAvailableException("예약할 수 없는 투어입니다")
        }
        
        // 정원 확인
        if (!tour.canAccommodate(participantCount)) {
            logger.warn("투어 정원 초과: tourId={}, 요청={}, 가능={}", 
                       tourId, participantCount, tour.getAvailableSpots())
            throw InsufficientCapacityException("투어 정원이 부족합니다. 가능한 자리: ${tour.getAvailableSpots()}")
        }
        
        // 중복 예약 확인
        if (bookingRepository.existsActiveBookingByUserAndTour(user, tour)) {
            logger.warn("중복 예약 시도: userId={}, tourId={}", userId, tourId)
            throw DuplicateBookingException("이미 예약된 투어입니다")
        }
        
        // 총 금액 계산
        val totalAmount = tour.price.multiply(BigDecimal.valueOf(participantCount.toLong()))
        
        val booking = Booking(
            user = user,
            tour = tour,
            participantCount = participantCount,
            totalAmount = totalAmount,
            status = BookingStatus.PENDING,
            specialRequests = specialRequests
        )
        
        // 투어 참가자 수 증가
        tour.increaseParticipants(participantCount)
        
        val savedBooking = bookingRepository.save(booking)
        logger.info("예약 생성 완료: bookingId={}, userId={}, tourId={}", 
                   savedBooking.id, userId, tourId)
        
        return savedBooking
    }

    /**
     * 예약 확정
     * 
     * @param bookingId 예약 ID
     * @param adminUserId 관리자 사용자 ID (권한 검사용)
     * @throws BookingNotFoundException 예약을 찾을 수 없는 경우
     * @throws UnauthorizedException 관리자 권한이 없는 경우
     */
    fun confirmBooking(bookingId: Long, adminUserId: Long): Booking {
        logger.info("예약 확정 시도: bookingId={}, adminUserId={}", bookingId, adminUserId)
        
        // 관리자 권한 검사
        val admin = userService.getUserById(adminUserId)
        if (!admin.isAdmin()) {
            throw UnauthorizedException("관리자 권한이 필요합니다")
        }
        
        val booking = getBookingById(bookingId)
        
        if (booking.status != BookingStatus.PENDING) {
            throw BookingException("대기중인 예약만 확정할 수 있습니다")
        }
        
        booking.confirm()
        
        val updatedBooking = bookingRepository.save(booking)
        logger.info("예약 확정 완료: bookingId={}", bookingId)
        
        return updatedBooking
    }

    /**
     * 예약 취소
     * 
     * @param bookingId 예약 ID
     * @param userId 사용자 ID (본인 확인용)
     * @param reason 취소 사유
     * @throws BookingNotFoundException 예약을 찾을 수 없는 경우
     * @throws BookingNotCancellableException 취소할 수 없는 예약인 경우
     * @throws UnauthorizedException 권한이 없는 경우
     */
    fun cancelBooking(bookingId: Long, userId: Long, reason: String? = null): Booking {
        logger.info("예약 취소 시도: bookingId={}, userId={}", bookingId, userId)
        
        val booking = getBookingById(bookingId)
        val user = userService.getUserById(userId)
        
        // 본인 예약이거나 관리자인지 확인
        if (booking.user.id != userId && !user.isAdmin()) {
            throw UnauthorizedException("본인의 예약만 취소할 수 있습니다")
        }
        
        // 취소 가능 여부 확인
        if (!booking.isCancellable()) {
            logger.warn("취소 불가능한 예약: bookingId={}, status={}, tourStartDate={}", 
                       bookingId, booking.status, booking.tour.startDate)
            throw BookingNotCancellableException("취소할 수 없는 예약입니다")
        }
        
        // 투어 참가자 수 감소
        booking.tour.decreaseParticipants(booking.participantCount)
        
        booking.cancel(reason)
        
        val updatedBooking = bookingRepository.save(booking)
        logger.info("예약 취소 완료: bookingId={}", bookingId)
        
        return updatedBooking
    }

    /**
     * 예약 완료 처리
     * 
     * @param bookingId 예약 ID
     * @param adminUserId 관리자 사용자 ID
     */
    fun completeBooking(bookingId: Long, adminUserId: Long): Booking {
        logger.info("예약 완료 처리 시도: bookingId={}, adminUserId={}", bookingId, adminUserId)
        
        // 관리자 권한 검사
        val admin = userService.getUserById(adminUserId)
        if (!admin.isAdmin()) {
            throw UnauthorizedException("관리자 권한이 필요합니다")
        }
        
        val booking = getBookingById(bookingId)
        
        if (booking.status != BookingStatus.CONFIRMED) {
            throw BookingException("확정된 예약만 완료 처리할 수 있습니다")
        }
        
        booking.complete()
        
        val updatedBooking = bookingRepository.save(booking)
        logger.info("예약 완료 처리 완료: bookingId={}", bookingId)
        
        return updatedBooking
    }

    /**
     * 예약 조회 (ID)
     */
    @Transactional(readOnly = true)
    fun getBookingById(id: Long): Booking {
        return bookingRepository.findById(id)
            .orElseThrow { BookingNotFoundException("예약을 찾을 수 없습니다: ID $id") }
    }

    /**
     * 사용자별 예약 내역 조회
     */
    @Transactional(readOnly = true)
    fun getUserBookings(userId: Long, pageable: Pageable): Page<Booking> {
        val user = userService.getUserById(userId)
        return bookingRepository.findByUser(user, pageable)
    }

    /**
     * 사용자별 상태별 예약 조회
     */
    @Transactional(readOnly = true)
    fun getUserBookingsByStatus(userId: Long, status: BookingStatus, pageable: Pageable): Page<Booking> {
        val user = userService.getUserById(userId)
        return bookingRepository.findByUserAndStatus(user, status, pageable)
    }

    /**
     * 투어별 예약 내역 조회
     */
    @Transactional(readOnly = true)
    fun getTourBookings(tourId: Long, pageable: Pageable): Page<Booking> {
        val tour = tourService.getTourById(tourId)
        return bookingRepository.findByTour(tour, pageable)
    }

    /**
     * 투어별 상태별 예약 조회
     */
    @Transactional(readOnly = true)
    fun getTourBookingsByStatus(tourId: Long, status: BookingStatus, pageable: Pageable): Page<Booking> {
        val tour = tourService.getTourById(tourId)
        return bookingRepository.findByTourAndStatus(tour, status, pageable)
    }

    /**
     * 사용자 예약 내역 조회 (날짜 범위)
     */
    @Transactional(readOnly = true)
    fun getUserBookingsByDateRange(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable
    ): Page<Booking> {
        val user = userService.getUserById(userId)
        return bookingRepository.findUserBookingsByDateRange(user, startDate, endDate, pageable)
    }

    /**
     * 취소 가능한 예약 조회
     */
    @Transactional(readOnly = true)
    fun getCancellableBookings(userId: Long, pageable: Pageable): Page<Booking> {
        val user = userService.getUserById(userId)
        return bookingRepository.findCancellableBookingsByUser(user, LocalDateTime.now(), pageable)
    }

    /**
     * 예약 특별 요청사항 업데이트
     */
    fun updateSpecialRequests(bookingId: Long, userId: Long, specialRequests: String?): Booking {
        logger.info("특별 요청사항 업데이트 시도: bookingId={}, userId={}", bookingId, userId)
        
        val booking = getBookingById(bookingId)
        val user = userService.getUserById(userId)
        
        // 본인 예약이거나 관리자인지 확인
        if (booking.user.id != userId && !user.isAdmin()) {
            throw UnauthorizedException("본인의 예약만 수정할 수 있습니다")
        }
        
        // 확정되지 않은 예약만 수정 가능
        if (booking.status !in listOf(BookingStatus.PENDING, BookingStatus.CONFIRMED)) {
            throw BookingException("대기중이거나 확정된 예약만 수정할 수 있습니다")
        }
        
        booking.updateSpecialRequests(specialRequests)
        
        val updatedBooking = bookingRepository.save(booking)
        logger.info("특별 요청사항 업데이트 완료: bookingId={}", bookingId)
        
        return updatedBooking
    }

    /**
     * 예약 생성 전 유효성 검사
     */
    private fun validateBookingCreation(userId: Long, tourId: Long, participantCount: Int) {
        if (participantCount <= 0) {
            throw InvalidParticipantCountException("참가자 수는 1명 이상이어야 합니다")
        }
        
        if (participantCount > 50) { // 최대 참가자 수 제한
            throw InvalidParticipantCountException("참가자 수는 50명을 초과할 수 없습니다")
        }
    }

    /**
     * 상태별 예약 수 조회
     */
    @Transactional(readOnly = true)
    fun getBookingCountByStatus(status: BookingStatus): Long {
        return bookingRepository.countByStatus(status)
    }

    /**
     * 투어의 실제 예약 참가자 수 계산 및 동기화
     */
    fun syncTourParticipants(tourId: Long): Int {
        val tour = tourService.getTourById(tourId)
        val actualParticipants = bookingRepository.sumActiveParticipantsByTour(tour)
        
        if (tour.currentParticipants != actualParticipants) {
            logger.info("투어 참가자 수 동기화: tourId={}, 기존={}, 실제={}", 
                       tourId, tour.currentParticipants, actualParticipants)
            
            // 참가자 수 직접 업데이트
            val updatedTour = tour.copy(currentParticipants = actualParticipants)
            // tourRepository.save(updatedTour) // TourService에서 해야 하므로 별도 구현 필요
        }
        
        return actualParticipants
    }
}