package com.gftour

import com.gftour.entity.User
import com.gftour.enums.UserRole
import com.gftour.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class DatabaseIntegrationTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should save and retrieve user`() {
        // Given
        val user = User().apply {
            username = "testuser"
            email = "test@example.com"
            password = "password"
            fullName = "Test User"
            role = UserRole.USER
        }

        // When
        val savedUser = userRepository.save(user)

        // Then
        assertNotNull(savedUser.id)
        assertEquals("testuser", savedUser.username)
        assertEquals("test@example.com", savedUser.email)
        assertEquals(UserRole.USER, savedUser.role)
        assertTrue(savedUser.enabled)
    }

    @Test
    fun `should find user by username`() {
        // Given
        val user = User().apply {
            username = "findme"
            email = "findme@example.com"
            password = "password"
            fullName = "Find Me"
            role = UserRole.USER
        }
        userRepository.save(user)

        // When
        val foundUser = userRepository.findByUsername("findme")

        // Then
        assertTrue(foundUser.isPresent)
        assertEquals("findme", foundUser.get().username)
    }

    @Test
    fun `should check if username exists`() {
        // Given
        val user = User().apply {
            username = "existinguser"
            email = "existing@example.com"
            password = "password"
            fullName = "Existing User"
            role = UserRole.USER
        }
        userRepository.save(user)

        // When & Then
        assertTrue(userRepository.existsByUsername("existinguser"))
        assertFalse(userRepository.existsByUsername("nonexistentuser"))
    }
}