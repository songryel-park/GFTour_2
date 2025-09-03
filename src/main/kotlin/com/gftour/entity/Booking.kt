package com.gftour.entity

import com.gftour.enums.BookingStatus
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "bookings")
@EntityListeners(AuditingEntityListener::class)
class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    var tour: Tour? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

    var participantCount: Int = 0

    @Column(nullable = false, precision = 10, scale = 2)
    var totalPrice: BigDecimal = BigDecimal.ZERO

    var bookingDate: LocalDateTime = LocalDateTime.now()

    @Enumerated(EnumType.STRING)
    var status: BookingStatus = BookingStatus.PENDING

    @Column(columnDefinition = "TEXT")
    var specialRequests: String = ""

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @OneToOne(mappedBy = "booking", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var review: Review? = null
}