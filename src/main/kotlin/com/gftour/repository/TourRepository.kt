package com.gftour.repository

import com.gftour.entity.Tour
import com.gftour.entity.User
import com.gftour.enums.TourStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface TourRepository : JpaRepository<Tour, Long> {
    fun findByStatus(status: TourStatus): List<Tour>
    fun findByCreatedBy(createdBy: User): List<Tour>
    fun findByLocationContainingIgnoreCase(location: String): List<Tour>
    fun findByStartDateBetween(startDate: LocalDate, endDate: LocalDate): List<Tour>
}