package com.gftour.repository

import com.gftour.entity.Booking
import com.gftour.entity.Tour
import com.gftour.entity.User
import com.gftour.enums.BookingStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByUser(user: User): List<Booking>
    fun findByTour(tour: Tour): List<Booking>
    fun findByStatus(status: BookingStatus): List<Booking>
    fun findByUserAndStatus(user: User, status: BookingStatus): List<Booking>
    fun findByTourAndStatus(tour: Tour, status: BookingStatus): List<Booking>
    fun findByBookingDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Booking>
    
    @Query("SELECT b FROM Booking b WHERE b.user = :user ORDER BY b.createdAt DESC")
    fun findByUserOrderByCreatedAtDesc(user: User): List<Booking>
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.tour = :tour AND b.status IN ('CONFIRMED', 'COMPLETED')")
    fun countConfirmedBookingsByTour(tour: Tour): Long
    
    @Query("SELECT SUM(b.participantCount) FROM Booking b WHERE b.tour = :tour AND b.status IN ('CONFIRMED', 'COMPLETED')")
    fun sumParticipantsByTour(tour: Tour): Int?
}