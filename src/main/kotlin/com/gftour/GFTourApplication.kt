package com.gftour

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * GFTour 애플리케이션의 메인 클래스
 * Spring Boot 애플리케이션의 진입점
 */
@SpringBootApplication
class GFTourApplication

fun main(args: Array<String>) {
    runApplication<GFTourApplication>(*args)
}