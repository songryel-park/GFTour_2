package com.gftour

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GfTourApplication

fun main(args: Array<String>) {
    runApplication<GfTourApplication>(*args)
}