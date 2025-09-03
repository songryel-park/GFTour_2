package com.gftour

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.Banner
import org.springframework.boot.builder.SpringApplicationBuilder
import jakarta.annotation.PostConstruct
import java.util.*

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
@EnableAsync
@ConfigurationPropertiesScan
class GfTourApplication {

    @PostConstruct
    fun setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
    }
}

fun main(args: Array<String>) {
    SpringApplicationBuilder(GfTourApplication::class.java)
        .bannerMode(Banner.Mode.CONSOLE)
        .run(*args)
}