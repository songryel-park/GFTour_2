package com.gftour.repository

import com.gftour.entity.Booking
import com.gftour.entity.Tour
import com.gftour.entity.User
import com.gftour.enums.BookingStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByUser(user: User): List<Booking>
    fun findByTour(tour: Tour): List<Booking>
    fun findByStatus(status: BookingStatus): List<Booking>
    fun findByUserAndStatus(user: User, status: BookingStatus): List<Booking>
}