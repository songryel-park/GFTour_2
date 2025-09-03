package com.gftour.entity

import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 예약 엔티티
 * 고객의 투어 예약 정보를 관리
 */
@Entity
@Table(name = "bookings")
data class Booking(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    val tour: Tour,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    val customer: Customer,
    
    val participantCount: Int = 1,
    
    @DecimalMin(value = "0.0", message = "총 가격은 0 이상이어야 합니다")
    val totalPrice: BigDecimal = BigDecimal.ZERO,
    
    @Enumerated(EnumType.STRING)
    val status: BookingStatus = BookingStatus.PENDING,
    
    val specialRequests: String? = null,
    
    val bookingDate: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class BookingStatus {
    PENDING, CONFIRMED, PAID, CANCELLED, COMPLETED
}