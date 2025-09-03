package com.gftour.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 예약 엔티티
 * 사용자의 투어 예약 정보를 관리
 */
@Entity
@Table(name = "bookings")
data class Booking(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    val tour: Tour,

    @Column(nullable = false)
    val participantCount: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    val totalAmount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: BookingStatus = BookingStatus.PENDING,

    @Column(length = 500)
    var specialRequests: String? = null,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column
    var cancelledAt: LocalDateTime? = null,

    @Column(length = 500)
    var cancellationReason: String? = null
) {
    /**
     * 예약 상태 변경
     */
    fun changeStatus(newStatus: BookingStatus) {
        this.status = newStatus
        if (newStatus == BookingStatus.CANCELLED && cancelledAt == null) {
            this.cancelledAt = LocalDateTime.now()
        }
    }

    /**
     * 예약 확정
     */
    fun confirm() {
        changeStatus(BookingStatus.CONFIRMED)
    }

    /**
     * 예약 취소
     */
    fun cancel(reason: String? = null) {
        changeStatus(BookingStatus.CANCELLED)
        this.cancellationReason = reason
    }

    /**
     * 예약 완료 처리
     */
    fun complete() {
        changeStatus(BookingStatus.COMPLETED)
    }

    /**
     * 취소 가능 여부 확인
     */
    fun isCancellable(): Boolean {
        return status in listOf(BookingStatus.PENDING, BookingStatus.CONFIRMED) &&
               tour.startDate.isAfter(LocalDateTime.now())
    }

    /**
     * 활성 예약 여부 확인 (취소되지 않은 예약)
     */
    fun isActive(): Boolean {
        return status != BookingStatus.CANCELLED
    }

    /**
     * 특별 요청사항 업데이트
     */
    fun updateSpecialRequests(requests: String?) {
        this.specialRequests = requests
    }
}

/**
 * 예약 상태 열거형
 */
enum class BookingStatus {
    PENDING,    // 대기중
    CONFIRMED,  // 확정
    CANCELLED,  // 취소
    COMPLETED   // 완료
}