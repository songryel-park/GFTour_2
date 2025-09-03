package com.gftour.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 투어 엔티티
 * 투어 상품 정보를 관리
 */
@Entity
@Table(name = "tours")
data class Tour(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 200)
    val title: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(nullable = false, length = 100)
    val location: String,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Column(nullable = false)
    val maxParticipants: Int,

    @Column(nullable = false)
    var currentParticipants: Int = 0,

    @Column(nullable = false)
    val startDate: LocalDateTime,

    @Column(nullable = false)
    val endDate: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: TourStatus = TourStatus.ACTIVE,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "tour", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val bookings: MutableList<Booking> = mutableListOf()
) {
    /**
     * 투어 상태 변경
     */
    fun changeStatus(newStatus: TourStatus) {
        this.status = newStatus
    }

    /**
     * 참가자 수 증가
     */
    fun increaseParticipants(count: Int) {
        this.currentParticipants += count
    }

    /**
     * 참가자 수 감소
     */
    fun decreaseParticipants(count: Int) {
        this.currentParticipants = maxOf(0, this.currentParticipants - count)
    }

    /**
     * 예약 가능 여부 확인
     */
    fun isBookable(): Boolean {
        return status == TourStatus.ACTIVE && 
               currentParticipants < maxParticipants &&
               startDate.isAfter(LocalDateTime.now())
    }

    /**
     * 남은 자리 수 조회
     */
    fun getAvailableSpots(): Int {
        return maxOf(0, maxParticipants - currentParticipants)
    }

    /**
     * 특정 인원 예약 가능 여부 확인
     */
    fun canAccommodate(participantCount: Int): Boolean {
        return getAvailableSpots() >= participantCount
    }

    /**
     * 투어 종료 여부 확인
     */
    fun isCompleted(): Boolean {
        return LocalDateTime.now().isAfter(endDate)
    }
}

/**
 * 투어 상태 열거형
 */
enum class TourStatus {
    ACTIVE,     // 활성 (예약 가능)
    INACTIVE,   // 비활성 (예약 불가)
    CANCELLED   // 취소됨
}