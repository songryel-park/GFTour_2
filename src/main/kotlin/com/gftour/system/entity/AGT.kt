package com.gftour.system.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Entity
@Table(name = "agts")
data class AGT(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    
    @NotBlank(message = "AGT 이름은 필수입니다")
    @Column(nullable = false, length = 100)
    val name: String = "",
    
    @Column(name = "contact_person", length = 100)
    val contactPerson: String? = null,
    
    @Column(name = "phone_number", length = 20)
    val phoneNumber: String? = null,
    
    @Column(length = 100)
    val email: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val address: String? = null,
    
    @Column(length = 100)
    val region: String? = null,
    
    @Column(length = 50)
    val country: String? = null,
    
    @Column(name = "business_license", length = 50)
    val businessLicense: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val notes: String? = null,
    
    @Column(nullable = false)
    val active: Boolean = true,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)