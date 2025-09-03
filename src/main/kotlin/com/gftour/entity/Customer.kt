package com.gftour.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 고객 엔티티
 * 고객 정보 및 여행 이력 관리
 */
@Entity
@Table(name = "customers")
data class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @NotBlank(message = "고객명은 필수입니다")
    @Size(max = 100)
    val name: String,
    
    @Size(max = 20)
    val phone: String? = null,
    
    @Size(max = 100)
    val email: String? = null,
    
    @Size(max = 500)
    val address: String? = null,
    
    val birthDate: LocalDate? = null,
    
    @Enumerated(EnumType.STRING)
    val customerType: CustomerType = CustomerType.REGULAR,
    
    @Size(max = 1000)
    val notes: String? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class CustomerType {
    REGULAR, VIP, CORPORATE
}