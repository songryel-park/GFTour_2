package com.gftour.repository

import com.gftour.entity.Tour
import com.gftour.entity.User
import com.gftour.enums.TourCategory
import com.gftour.enums.TourStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
interface TourRepository : JpaRepository<Tour, Long> {
    fun findByStatus(status: TourStatus): List<Tour>
    fun findByCategory(category: TourCategory): List<Tour>
    fun findByCreatedBy(user: User): List<Tour>
    fun findByStatusAndCategory(status: TourStatus, category: TourCategory): List<Tour>
    fun findByPriceBetween(minPrice: BigDecimal, maxPrice: BigDecimal): List<Tour>
    fun findByStartDateAfter(date: LocalDate): List<Tour>
    fun findByTitleContainingIgnoreCase(title: String): List<Tour>
    fun findByLocationContainingIgnoreCase(location: String): List<Tour>
    
    @Query("SELECT t FROM Tour t WHERE t.status = 'ACTIVE' AND t.startDate >= :currentDate")
    fun findAvailableTours(currentDate: LocalDate): List<Tour>
}