package com.gftour

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PostConstruct
import java.util.TimeZone

/**
 * Main Spring Boot Application class for Good Feel Tour (GfTour) web application.
 * 
 * This application provides tour management and booking functionality with:
 * - JPA repositories for data persistence
 * - Asynchronous processing for file uploads and heavy operations  
 * - Transaction management for data consistency
 * - Seoul/Asia timezone configuration
 * - Production-ready configuration with graceful startup/shutdown
 */
@SpringBootApplication(scanBasePackages = ["com.gftour"])
@EnableJpaRepositories(basePackages = ["com.gftour.repository"])
@EnableTransactionManagement
@EnableAsync
@Configuration
class GfTourApplication {

    /**
     * Configure application timezone to Seoul/Asia on startup
     */
    @PostConstruct
    fun configureTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
    }
}

/**
 * Main function to start the Spring Boot application
 */
fun main(args: Array<String>) {
    runApplication<GfTourApplication>(*args)
}