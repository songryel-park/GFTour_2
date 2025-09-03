package com.gftour.config

import com.gftour.entity.Booking
import com.gftour.entity.Tour
import com.gftour.entity.User
import com.gftour.enums.BookingStatus
import com.gftour.enums.TourStatus
import com.gftour.enums.UserRole
import com.gftour.repository.BookingRepository
import com.gftour.repository.TourRepository
import com.gftour.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.math.BigDecimal
import java.time.LocalDate

@Configuration
class DataSeederConfig {

    @Bean
    fun dataSeeder(
        userRepository: UserRepository,
        tourRepository: TourRepository,
        bookingRepository: BookingRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            if (userRepository.count() == 0L) {
                // Create sample user (demonstrating User entity structure without fullName)
                val sampleUser = User(
                    username = "johndoe",
                    password = "password123",
                    email = "john.doe@example.com",
                    phoneNumber = "010-1234-5678",
                    role = UserRole.USER,
                    enabled = true
                )
                val savedUser = userRepository.save(sampleUser)

                // Create sample admin user 
                val adminUser = User(
                    username = "admin",
                    password = "admin123",
                    email = "admin@gftour.com",
                    phoneNumber = "010-9999-0000",
                    role = UserRole.ADMIN,
                    enabled = true
                )
                val savedAdmin = userRepository.save(adminUser)

                // Create sample tour (demonstrating Tour entity structure without category field)
                val sampleTour = Tour(
                    title = "Seoul City Tour",
                    description = "Explore the beautiful sights of Seoul including palaces, markets, and modern districts.",
                    location = "Seoul, South Korea",
                    price = BigDecimal("89000"),
                    maxParticipants = 20,
                    duration = 1,
                    status = TourStatus.ACTIVE,
                    startDate = LocalDate.now().plusDays(7),
                    endDate = LocalDate.now().plusDays(7),
                    createdBy = savedAdmin
                )
                val savedTour = tourRepository.save(sampleTour)

                // Create sample booking (demonstrating Booking entity structure)
                val sampleBooking = Booking(
                    tour = savedTour,
                    user = savedUser,
                    participantCount = 2,
                    totalPrice = BigDecimal("178000"),
                    bookingDate = LocalDate.now(),
                    status = BookingStatus.CONFIRMED,
                    specialRequests = "Vegetarian meals preferred"
                )
                bookingRepository.save(sampleBooking)

                println("Sample data seeded successfully!")
                println("✅ User entity created without fullName field")
                println("✅ Tour entity created without category field or TourCategory enum")
                println("✅ Booking entity created with all required fields")
                println("❌ Review entity and ReviewRepository NOT created (as requested)")
            }
        }
    }
}