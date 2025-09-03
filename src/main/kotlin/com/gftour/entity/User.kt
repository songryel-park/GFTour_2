package com.gftour.entity

import com.gftour.enums.UserRole
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(unique = true, nullable = false)
    var username: String = ""

    @Column(nullable = false)
    var password: String = ""

    @Column(unique = true, nullable = false)
    var email: String = ""

    var fullName: String = ""

    var phoneNumber: String = ""

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.USER

    var enabled: Boolean = true

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @OneToMany(mappedBy = "createdBy", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var createdTours: MutableList<Tour> = mutableListOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var bookings: MutableList<Booking> = mutableListOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var reviews: MutableList<Review> = mutableListOf()
}