package com.gftour.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 투어 엔티티
 * 여행 패키지 정보를 관리
 */
@Entity
@Table(name = "tours")
data class Tour(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @NotBlank(message = "투어명은 필수입니다")
    @Size(max = 200)
    val name: String,
    
    @Size(max = 1000)
    val description: String? = null,
    
    @Size(max = 100)
    val destination: String? = null,
    
    val startDate: LocalDate,
    val endDate: LocalDate,
    
    @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다")
    val price: BigDecimal = BigDecimal.ZERO,
    
    val maxParticipants: Int = 0,
    val currentParticipants: Int = 0,
    
    @Enumerated(EnumType.STRING)
    val status: TourStatus = TourStatus.PLANNED,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    val createdBy: User? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TourStatus {
    PLANNED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
}