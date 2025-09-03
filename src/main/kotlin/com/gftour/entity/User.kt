package com.gftour.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * 사용자 엔티티
 * 시스템 사용자 정보를 관리
 */
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(max = 50)
    @Column(unique = true)
    val username: String,
    
    @NotBlank(message = "비밀번호는 필수입니다") 
    val password: String,
    
    @NotBlank(message = "이메일은 필수입니다")
    @Size(max = 100)
    val email: String,
    
    @Size(max = 100)
    val name: String? = null,
    
    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.USER,
    
    val active: Boolean = true,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class UserRole {
    ADMIN, MANAGER, USER
}