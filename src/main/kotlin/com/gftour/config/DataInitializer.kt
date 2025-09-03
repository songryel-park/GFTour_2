package com.gftour.config

import com.gftour.entity.User
import com.gftour.entity.UserRole
import com.gftour.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (userRepository.count() == 0L) {
            // Create admin user
            val adminUser = User(
                email = "admin@gftour.com",
                password = passwordEncoder.encode("admin123"),
                name = "시스템 관리자",
                role = UserRole.ADMIN
            )
            userRepository.save(adminUser)

            // Create manager user
            val managerUser = User(
                email = "manager@gftour.com",
                password = passwordEncoder.encode("manager123"),
                name = "매니저",
                role = UserRole.MANAGER
            )
            userRepository.save(managerUser)

            // Create regular user
            val regularUser = User(
                email = "user@gftour.com",
                password = passwordEncoder.encode("user123"),
                name = "일반 사용자",
                role = UserRole.USER
            )
            userRepository.save(regularUser)

            println("초기 사용자 데이터가 생성되었습니다:")
            println("관리자: admin@gftour.com / admin123")
            println("매니저: manager@gftour.com / manager123")
            println("사용자: user@gftour.com / user123")
        }
    }
}