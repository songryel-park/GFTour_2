package com.gftour.service

import com.gftour.entity.User
import com.gftour.entity.UserRole
import com.gftour.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun getTotalUsers(): Long {
        return userRepository.count()
    }

    fun getActiveAgentsCount(): Long {
        return userRepository.findActiveUsersByRole(UserRole.AGENT).size.toLong()
    }

    fun createUser(
        email: String,
        fullName: String,
        password: String,
        role: UserRole,
        phone: String? = null,
        department: String? = null,
        commissionRate: Double? = null,
        territory: String? = null
    ): User {
        val user = User(
            email = email,
            fullName = fullName,
            password = passwordEncoder.encode(password),
            role = role,
            phone = phone,
            department = department,
            commissionRate = commissionRate,
            territory = territory
        )
        return userRepository.save(user)
    }

    fun updateLastLogin(userId: Long) {
        val user = userRepository.findById(userId).orElse(null)
        user?.let {
            val updatedUser = user.copy(lastLoginAt = LocalDateTime.now())
            userRepository.save(updatedUser)
        }
    }

    fun searchUsers(keyword: String): List<User> {
        return userRepository.searchByName(keyword)
    }

    fun getAgentsByTerritory(territory: String): List<User> {
        return userRepository.findAgentsByTerritory(territory)
    }

    fun getUsersByRole(role: UserRole): List<User> {
        return userRepository.findActiveUsersByRole(role)
    }
}