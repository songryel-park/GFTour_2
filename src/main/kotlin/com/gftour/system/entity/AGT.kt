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
    
    @Column(name = "agencies", length = 100)
    val agencies: String? = null,

    @Column(name = "agencies", length = 100)
    val manager: String? = null,

    @Column(columnDefinition = "TEXT")
    val address: String? = null,

    @Column(length = 100)
    val region: String? = null,
    
    @Column(name = "tellNumber", length = 20)
    val tellNumber: String? = null,
    
    @Column(length = 100)
    val email: String? = null,
    
    @Column(length = 50)
    val post: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val notes: String? = null,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)