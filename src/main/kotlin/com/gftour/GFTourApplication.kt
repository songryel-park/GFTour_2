package com.gftour

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GFTourApplication

fun main(args: Array<String>) {
    runApplication<GFTourApplication>(*args)
}