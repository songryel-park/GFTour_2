package com.gftour.system.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.time.LocalDateTime

@Entity
@Table(name = "customers")
data class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    
    @NotBlank(message = "이름은 필수입니다")
    @Column(nullable = false, length = 100)
    val name: String = "",
    
    @Column(nullable = false)
    val age: Int = 0,
    
    @NotBlank(message = "여권번호는 필수입니다")
    @Pattern(regexp = "[A-Z0-9]{8,9}", message = "올바른 여권번호 형식이 아닙니다")
    @Column(name = "passport_number", nullable = false, length = 20)
    val passportNumber: String = "",
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_record_id")
    val fileRecord: FileRecord? = null,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)