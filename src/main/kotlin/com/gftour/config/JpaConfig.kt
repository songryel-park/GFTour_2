package com.gftour.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * JPA 설정
 * JPA 리포지토리 및 트랜잭션 관리 활성화
 */
@Configuration
@EnableJpaRepositories(basePackages = ["com.gftour.repository"])
@EnableTransactionManagement
class JpaConfig