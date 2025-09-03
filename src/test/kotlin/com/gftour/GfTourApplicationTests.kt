package com.gftour

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

/**
 * Integration test for GfTourApplication main class
 */
@SpringBootTest
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
])
class GfTourApplicationTests {

    @Test
    fun contextLoads() {
        // Test that the Spring context loads successfully
        // This will verify all annotations and configurations are correct
    }
}