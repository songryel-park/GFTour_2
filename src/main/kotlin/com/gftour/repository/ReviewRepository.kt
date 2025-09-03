package com.gftour.repository

import com.gftour.entity.Review
import com.gftour.entity.Tour
import com.gftour.entity.User
import com.gftour.entity.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByTour(tour: Tour): List<Review>
    fun findByUser(user: User): List<Review>
    fun findByBooking(booking: Booking): Review?
    fun findByRating(rating: Int): List<Review>
    fun findByTourOrderByCreatedAtDesc(tour: Tour): List<Review>
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.tour = :tour")
    fun findAverageRatingByTour(tour: Tour): Double?
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.tour = :tour")
    fun countReviewsByTour(tour: Tour): Long
    
    @Query("SELECT r FROM Review r WHERE r.tour = :tour AND r.rating >= :minRating ORDER BY r.createdAt DESC")
    fun findByTourAndRatingGreaterThanEqualOrderByCreatedAtDesc(tour: Tour, minRating: Int): List<Review>
}