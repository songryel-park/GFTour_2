package com.gftour.repository

import com.gftour.domain.Booking
import com.gftour.domain.BookingStatus
import com.gftour.domain.Tour
import com.gftour.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

/**
 * 예약 데이터 접근 인터페이스
 */
interface BookingRepository : JpaRepository<Booking, Long> {
    
    /**
     * 사용자별 예약 조회
     */
    fun findByUser(user: User, pageable: Pageable): Page<Booking>
    
    /**
     * 사용자별 상태별 예약 조회
     */
    fun findByUserAndStatus(user: User, status: BookingStatus, pageable: Pageable): Page<Booking>
    
    /**
     * 투어별 예약 조회
     */
    fun findByTour(tour: Tour, pageable: Pageable): Page<Booking>
    
    /**
     * 투어별 상태별 예약 조회
     */
    fun findByTourAndStatus(tour: Tour, status: BookingStatus, pageable: Pageable): Page<Booking>
    
    /**
     * 사용자가 특정 투어에 활성 예약이 있는지 확인
     */
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b 
        WHERE b.user = :user 
        AND b.tour = :tour 
        AND b.status IN ('PENDING', 'CONFIRMED')
    """)
    fun existsActiveBookingByUserAndTour(
        @Param("user") user: User, 
        @Param("tour") tour: Tour
    ): Boolean
    
    /**
     * 투어의 활성 예약 총 참가자 수 조회
     */
    @Query("""
        SELECT COALESCE(SUM(b.participantCount), 0) FROM Booking b 
        WHERE b.tour = :tour 
        AND b.status IN ('PENDING', 'CONFIRMED')
    """)
    fun sumActiveParticipantsByTour(@Param("tour") tour: Tour): Int
    
    /**
     * 사용자의 예약 내역 조회 (날짜 범위)
     */
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.user = :user 
        AND b.createdAt BETWEEN :startDate AND :endDate 
        ORDER BY b.createdAt DESC
    """)
    fun findUserBookingsByDateRange(
        @Param("user") user: User,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime,
        pageable: Pageable
    ): Page<Booking>
    
    /**
     * 취소 가능한 예약 조회
     */
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.user = :user 
        AND b.status IN ('PENDING', 'CONFIRMED') 
        AND b.tour.startDate > :currentTime
    """)
    fun findCancellableBookingsByUser(
        @Param("user") user: User,
        @Param("currentTime") currentTime: LocalDateTime,
        pageable: Pageable
    ): Page<Booking>
    
    /**
     * 상태별 예약 수 조회
     */
    fun countByStatus(status: BookingStatus): Long
}