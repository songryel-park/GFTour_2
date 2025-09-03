package com.gftour.service

import com.gftour.domain.Tour
import com.gftour.domain.TourStatus
import com.gftour.exception.*
import com.gftour.repository.TourRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 투어 서비스
 * 투어 생성, 수정, 삭제, 조회 및 검색 관련 비즈니스 로직을 처리
 */
@Service
@Transactional
class TourService(
    private val tourRepository: TourRepository,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(TourService::class.java)

    /**
     * 투어 생성
     * 
     * @param title 투어 제목
     * @param description 투어 설명
     * @param location 투어 위치
     * @param price 투어 가격
     * @param maxParticipants 최대 참가자 수
     * @param startDate 투어 시작일
     * @param endDate 투어 종료일
     * @param adminUserId 관리자 사용자 ID (권한 검사용)
     * @return 생성된 투어 정보
     * @throws UnauthorizedException 관리자 권한이 없는 경우
     * @throws IllegalArgumentException 잘못된 입력값인 경우
     */
    fun createTour(
        title: String,
        description: String?,
        location: String,
        price: BigDecimal,
        maxParticipants: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        adminUserId: Long
    ): Tour {
        logger.info("투어 생성 시도: title={}, location={}, adminUserId={}", title, location, adminUserId)
        
        // 관리자 권한 검사
        val admin = userService.getUserById(adminUserId)
        if (!admin.isAdmin()) {
            logger.warn("관리자 권한 없음: adminUserId={}", adminUserId)
            throw UnauthorizedException("관리자 권한이 필요합니다")
        }
        
        // 입력값 검증
        validateTourData(title, price, maxParticipants, startDate, endDate)
        
        val tour = Tour(
            title = title,
            description = description,
            location = location,
            price = price,
            maxParticipants = maxParticipants,
            startDate = startDate,
            endDate = endDate,
            status = TourStatus.ACTIVE
        )
        
        val savedTour = tourRepository.save(tour)
        logger.info("투어 생성 완료: id={}, title={}", savedTour.id, savedTour.title)
        
        return savedTour
    }

    /**
     * 투어 수정
     * 
     * @param tourId 투어 ID
     * @param title 투어 제목
     * @param description 투어 설명
     * @param location 투어 위치
     * @param price 투어 가격
     * @param maxParticipants 최대 참가자 수 (현재 참가자보다 적을 수 없음)
     * @param startDate 투어 시작일
     * @param endDate 투어 종료일
     * @param adminUserId 관리자 사용자 ID
     * @throws TourNotFoundException 투어를 찾을 수 없는 경우
     * @throws TourAlreadyStartedException 이미 시작된 투어인 경우
     * @throws IllegalArgumentException 잘못된 입력값인 경우
     */
    fun updateTour(
        tourId: Long,
        title: String,
        description: String?,
        location: String,
        price: BigDecimal,
        maxParticipants: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        adminUserId: Long
    ): Tour {
        logger.info("투어 수정 시도: tourId={}, adminUserId={}", tourId, adminUserId)
        
        // 관리자 권한 검사
        val admin = userService.getUserById(adminUserId)
        if (!admin.isAdmin()) {
            throw UnauthorizedException("관리자 권한이 필요합니다")
        }
        
        val tour = getTourById(tourId)
        
        // 이미 시작된 투어는 수정 불가
        if (LocalDateTime.now().isAfter(tour.startDate)) {
            logger.warn("이미 시작된 투어 수정 시도: tourId={}", tourId)
            throw TourAlreadyStartedException("이미 시작된 투어는 수정할 수 없습니다")
        }
        
        // 입력값 검증
        validateTourData(title, price, maxParticipants, startDate, endDate)
        
        // 최대 참가자 수가 현재 참가자보다 적으면 안됨
        if (maxParticipants < tour.currentParticipants) {
            throw IllegalArgumentException("최대 참가자 수는 현재 참가자 수(${ tour.currentParticipants })보다 적을 수 없습니다")
        }
        
        val updatedTour = tour.copy(
            title = title,
            description = description,
            location = location,
            price = price,
            maxParticipants = maxParticipants,
            startDate = startDate,
            endDate = endDate
        )
        
        val savedTour = tourRepository.save(updatedTour)
        logger.info("투어 수정 완료: tourId={}", tourId)
        
        return savedTour
    }

    /**
     * 투어 조회 (ID)
     */
    @Transactional(readOnly = true)
    fun getTourById(id: Long): Tour {
        return tourRepository.findById(id)
            .orElseThrow { TourNotFoundException("투어를 찾을 수 없습니다: ID $id") }
    }

    /**
     * 모든 투어 조회 (페이징)
     */
    @Transactional(readOnly = true)
    fun getAllTours(pageable: Pageable): Page<Tour> {
        return tourRepository.findAll(pageable)
    }

    /**
     * 활성 투어 조회
     */
    @Transactional(readOnly = true)
    fun getActiveTours(pageable: Pageable): Page<Tour> {
        return tourRepository.findByStatus(TourStatus.ACTIVE, pageable)
    }

    /**
     * 예약 가능한 투어 조회
     */
    @Transactional(readOnly = true)
    fun getAvailableTours(pageable: Pageable): Page<Tour> {
        return tourRepository.findAvailableTours(TourStatus.ACTIVE, LocalDateTime.now(), pageable)
    }

    /**
     * 인기 투어 조회 (예약률 높은 순)
     */
    @Transactional(readOnly = true)
    fun getPopularTours(pageable: Pageable): Page<Tour> {
        return tourRepository.findPopularTours(TourStatus.ACTIVE, pageable)
    }

    /**
     * 위치별 투어 검색
     */
    @Transactional(readOnly = true)
    fun searchToursByLocation(location: String, pageable: Pageable): Page<Tour> {
        return tourRepository.findByLocationContainingAndStatus(location, TourStatus.ACTIVE, pageable)
    }

    /**
     * 가격 범위별 투어 검색
     */
    @Transactional(readOnly = true)
    fun searchToursByPriceRange(
        minPrice: BigDecimal,
        maxPrice: BigDecimal,
        pageable: Pageable
    ): Page<Tour> {
        return tourRepository.findByPriceBetweenAndStatus(minPrice, maxPrice, TourStatus.ACTIVE, pageable)
    }

    /**
     * 날짜 범위별 투어 검색
     */
    @Transactional(readOnly = true)
    fun searchToursByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable
    ): Page<Tour> {
        return tourRepository.findByStartDateBetweenAndStatus(startDate, endDate, TourStatus.ACTIVE, pageable)
    }

    /**
     * 복합 필터로 투어 검색
     */
    @Transactional(readOnly = true)
    fun searchToursWithFilters(
        location: String?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        pageable: Pageable
    ): Page<Tour> {
        return tourRepository.findToursWithFilters(
            TourStatus.ACTIVE, location, minPrice, maxPrice, startDate, endDate, pageable
        )
    }

    /**
     * 투어 상태 변경
     */
    fun changeTourStatus(tourId: Long, newStatus: TourStatus, adminUserId: Long): Tour {
        logger.info("투어 상태 변경 시도: tourId={}, newStatus={}, adminUserId={}", 
                   tourId, newStatus, adminUserId)
        
        // 관리자 권한 검사
        val admin = userService.getUserById(adminUserId)
        if (!admin.isAdmin()) {
            throw UnauthorizedException("관리자 권한이 필요합니다")
        }
        
        val tour = getTourById(tourId)
        tour.changeStatus(newStatus)
        
        val updatedTour = tourRepository.save(tour)
        logger.info("투어 상태 변경 완료: tourId={}, newStatus={}", tourId, newStatus)
        
        return updatedTour
    }

    /**
     * 투어 취소
     */
    fun cancelTour(tourId: Long, adminUserId: Long): Tour {
        logger.info("투어 취소 시도: tourId={}, adminUserId={}", tourId, adminUserId)
        
        return changeTourStatus(tourId, TourStatus.CANCELLED, adminUserId)
    }

    /**
     * 투어 정원 확인
     */
    @Transactional(readOnly = true)
    fun checkTourCapacity(tourId: Long, requiredSpots: Int): Boolean {
        val tour = getTourById(tourId)
        return tour.canAccommodate(requiredSpots)
    }

    /**
     * 투어 예약 가능 여부 확인
     */
    @Transactional(readOnly = true)
    fun isTourBookable(tourId: Long, participantCount: Int): Boolean {
        val tour = getTourById(tourId)
        return tour.isBookable() && tour.canAccommodate(participantCount)
    }

    /**
     * 투어 추천 (특정 기준에 따른)
     */
    @Transactional(readOnly = true)
    fun getRecommendedTours(pageable: Pageable): Page<Tour> {
        // 예약 가능하고 인기 있는 투어 추천
        return getAvailableTours(pageable)
    }

    /**
     * 투어 데이터 유효성 검증
     */
    private fun validateTourData(
        title: String,
        price: BigDecimal,
        maxParticipants: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) {
        if (title.isBlank()) {
            throw IllegalArgumentException("투어 제목은 필수입니다")
        }
        
        if (price <= BigDecimal.ZERO) {
            throw IllegalArgumentException("투어 가격은 0보다 커야 합니다")
        }
        
        if (maxParticipants <= 0) {
            throw IllegalArgumentException("최대 참가자 수는 0보다 커야 합니다")
        }
        
        if (startDate.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("투어 시작일은 현재 시간 이후여야 합니다")
        }
        
        if (endDate.isBefore(startDate)) {
            throw IllegalArgumentException("투어 종료일은 시작일 이후여야 합니다")
        }
    }
}