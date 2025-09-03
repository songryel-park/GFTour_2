package com.gftour.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * AGT (에이전트) 엔티티
 * 여행사 에이전트 정보 관리
 */
@Entity
@Table(name = "agts")
data class AGT(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @NotBlank(message = "AGT 코드는 필수입니다")
    @Size(max = 20)
    @Column(unique = true)
    val agtCode: String,
    
    @NotBlank(message = "AGT명은 필수입니다")
    @Size(max = 100)
    val name: String,
    
    @Size(max = 500)
    val address: String? = null,
    
    @Size(max = 20)
    val phone: String? = null,
    
    @Size(max = 20)
    val fax: String? = null,
    
    @Size(max = 100)
    val contactPerson: String? = null,
    
    @Size(max = 100)
    val email: String? = null,
    
    @Enumerated(EnumType.STRING)
    val status: AGTStatus = AGTStatus.ACTIVE,
    
    @Size(max = 1000)
    val notes: String? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class AGTStatus {
    ACTIVE, INACTIVE, SUSPENDED
}