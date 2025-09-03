package com.gftour.entity

import com.gftour.enums.TourCategory
import com.gftour.enums.TourStatus
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "tours")
@EntityListeners(AuditingEntityListener::class)
class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false)
    var title: String = ""

    @Column(columnDefinition = "TEXT")
    var description: String = ""

    var location: String = ""

    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal = BigDecimal.ZERO

    var maxParticipants: Int = 0

    var duration: Int = 0 // 분 단위

    @Enumerated(EnumType.STRING)
    var category: TourCategory = TourCategory.NATURE

    @Enumerated(EnumType.STRING)
    var status: TourStatus = TourStatus.ACTIVE

    var startDate: LocalDate = LocalDate.now()

    var endDate: LocalDate = LocalDate.now()

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    var createdBy: User? = null

    @OneToMany(mappedBy = "tour", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var bookings: MutableList<Booking> = mutableListOf()

    @OneToMany(mappedBy = "tour", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var reviews: MutableList<Review> = mutableListOf()
}