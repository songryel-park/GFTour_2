package com.gftour.repository

import com.gftour.domain.Tour
import com.gftour.domain.TourStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 투어 데이터 접근 인터페이스
 */
interface TourRepository : JpaRepository<Tour, Long> {
    
    /**
     * 상태별 투어 조회
     */
    fun findByStatus(status: TourStatus, pageable: Pageable): Page<Tour>
    
    /**
     * 위치별 투어 검색
     */
    fun findByLocationContainingAndStatus(
        location: String, 
        status: TourStatus, 
        pageable: Pageable
    ): Page<Tour>
    
    /**
     * 가격 범위별 투어 검색
     */
    fun findByPriceBetweenAndStatus(
        minPrice: BigDecimal, 
        maxPrice: BigDecimal, 
        status: TourStatus, 
        pageable: Pageable
    ): Page<Tour>
    
    /**
     * 날짜 범위별 투어 검색
     */
    fun findByStartDateBetweenAndStatus(
        startDate: LocalDateTime, 
        endDate: LocalDateTime, 
        status: TourStatus, 
        pageable: Pageable
    ): Page<Tour>
    
    /**
     * 예약 가능한 투어 조회 (정원 미달 + 시작일이 미래)
     */
    @Query("""
        SELECT t FROM Tour t 
        WHERE t.status = :status 
        AND t.currentParticipants < t.maxParticipants 
        AND t.startDate > :currentTime
    """)
    fun findAvailableTours(
        @Param("status") status: TourStatus,
        @Param("currentTime") currentTime: LocalDateTime,
        pageable: Pageable
    ): Page<Tour>
    
    /**
     * 인기 투어 조회 (예약률 높은 순)
     */
    @Query("""
        SELECT t FROM Tour t 
        WHERE t.status = :status 
        ORDER BY (CAST(t.currentParticipants AS float) / t.maxParticipants) DESC
    """)
    fun findPopularTours(
        @Param("status") status: TourStatus,
        pageable: Pageable
    ): Page<Tour>
    
    /**
     * 복합 검색 (위치, 가격, 날짜)
     */
    @Query("""
        SELECT t FROM Tour t 
        WHERE t.status = :status 
        AND (:location IS NULL OR t.location LIKE %:location%) 
        AND (:minPrice IS NULL OR t.price >= :minPrice) 
        AND (:maxPrice IS NULL OR t.price <= :maxPrice) 
        AND (:startDate IS NULL OR t.startDate >= :startDate) 
        AND (:endDate IS NULL OR t.startDate <= :endDate)
    """)
    fun findToursWithFilters(
        @Param("status") status: TourStatus,
        @Param("location") location: String?,
        @Param("minPrice") minPrice: BigDecimal?,
        @Param("maxPrice") maxPrice: BigDecimal?,
        @Param("startDate") startDate: LocalDateTime?,
        @Param("endDate") endDate: LocalDateTime?,
        pageable: Pageable
    ): Page<Tour>
}