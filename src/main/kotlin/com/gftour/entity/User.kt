package com.gftour.entity

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(unique = true, nullable = false)
    val email: String,
    
    @Column(nullable = false)
    val fullName: String,
    
    @Column(nullable = false)
    private val password: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val lastLoginAt: LocalDateTime? = null,
    
    @Column
    val phone: String? = null,
    
    @Column
    val department: String? = null,
    
    // For agents - commission rates, territory, etc.
    @Column
    val commissionRate: Double? = null,
    
    @Column
    val territory: String? = null
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
    }

    override fun getPassword(): String = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = isActive

    override fun isAccountNonLocked(): Boolean = isActive

    override fun isCredentialsNonExpired(): Boolean = isActive

    override fun isEnabled(): Boolean = isActive
}

enum class UserRole {
    ADMIN,      // Full system access
    MANAGER,    // Management access with some restrictions
    AGENT,      // Travel agents with limited access
    VIEWER      // Read-only access
}