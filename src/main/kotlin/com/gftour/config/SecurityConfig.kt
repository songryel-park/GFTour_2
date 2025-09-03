package com.gftour.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
class SecurityConfig {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            // URL 패턴 기반 접근 제어 설정
            .authorizeHttpRequests { requests ->
                requests
                    // 정적 리소스 공개 접근
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                    // 공개 페이지 접근
                    .requestMatchers("/", "/home", "/tours/**").permitAll()
                    // 로그인/로그아웃 페이지 공개 접근
                    .requestMatchers("/login", "/logout").permitAll()
                    // 관리자 권한이 필요한 경로
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    // 사용자 권한이 필요한 API 경로
                    .requestMatchers("/api/user/**").hasRole("USER")
                    // 그 외 모든 요청은 인증 필요
                    .anyRequest().authenticated()
            }
            // 커스텀 로그인 설정
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=true")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .permitAll()
            }
            // 로그아웃 설정
            .logout { logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .permitAll()
            }
            // 세션 관리 설정
            .sessionManagement { session ->
                session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
                    .and()
                    .sessionFixation().changeSessionId()
                    .invalidSessionUrl("/login?expired=true")
            }
            // Remember-me 기능 설정
            .rememberMe { rememberMe ->
                rememberMe
                    .key("GFTour-RememberMe-Key")
                    .tokenValiditySeconds(14 * 24 * 60 * 60) // 2주
                    .userDetailsService(userDetailsService())
                    .rememberMeParameter("remember-me")
                    .rememberMeCookieName("remember-me")
            }
            // CSRF 보호 설정
            .csrf { csrf ->
                csrf
                    // API 엔드포인트에 대한 CSRF 보호 유지
                    .ignoringRequestMatchers("/api/public/**")
                    // CSRF 토큰을 헤더나 파라미터로 받을 수 있도록 설정
                    .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
            }
            // 예외 처리 설정
            .exceptionHandling { exceptions ->
                exceptions
                    .accessDeniedPage("/error/403")
                    .authenticationEntryPoint { request, response, authException ->
                        if (request.requestURI.startsWith("/api/")) {
                            response.sendError(401, "Unauthorized")
                        } else {
                            response.sendRedirect("/login")
                        }
                    }
            }
            // 보안 헤더 설정
            .headers { headers ->
                headers
                    .frameOptions().deny()
                    .contentTypeOptions().and()
                    .httpStrictTransportSecurity { hstsConfig ->
                        hstsConfig
                            .maxAgeInSeconds(31536000)
                            .includeSubDomains(true)
                    }
            }
            .build()
    }
    
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    
    @Bean
    fun userDetailsService(): org.springframework.security.core.userdetails.UserDetailsService {
        // 임시 인메모리 사용자 설정 (실제 환경에서는 데이터베이스나 외부 인증 서비스 사용)
        val userDetails = org.springframework.security.core.userdetails.User.withUsername("user")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build()
            
        val adminDetails = org.springframework.security.core.userdetails.User.withUsername("admin")
            .password(passwordEncoder().encode("admin"))
            .roles("ADMIN", "USER")
            .build()
            
        return org.springframework.security.provisioning.InMemoryUserDetailsManager(userDetails, adminDetails)
    }
}