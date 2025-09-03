package com.gftour.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

/**
 * 사용자 엔티티
 * 시스템에 등록된 사용자 정보를 관리
 */
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 100)
    val email: String,

    @Column(nullable = false, length = 50)
    val name: String,

    @Column(nullable = false, length = 255)
    var password: String,

    @Column(length = 20)
    val phone: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: UserStatus = UserStatus.ACTIVE,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val role: UserRole = UserRole.USER,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val bookings: MutableList<Booking> = mutableListOf()
) {
    /**
     * 비밀번호 변경
     */
    fun changePassword(newPassword: String) {
        this.password = newPassword
    }

    /**
     * 사용자 상태 변경
     */
    fun changeStatus(newStatus: UserStatus) {
        this.status = newStatus
    }

    /**
     * 활성 사용자 여부 확인
     */
    fun isActive(): Boolean = status == UserStatus.ACTIVE

    /**
     * 관리자 권한 여부 확인
     */
    fun isAdmin(): Boolean = role == UserRole.ADMIN
}

/**
 * 사용자 상태 열거형
 */
enum class UserStatus {
    ACTIVE,    // 활성
    INACTIVE,  // 비활성
    SUSPENDED  // 정지
}

/**
 * 사용자 역할 열거형
 */
enum class UserRole {
    USER,   // 일반 사용자
    ADMIN   // 관리자
}