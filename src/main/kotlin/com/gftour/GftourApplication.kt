package com.gftour

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GftourApplication

fun main(args: Array<String>) {
    runApplication<GftourApplication>(*args)
}