package com.gftour.config

import com.gftour.security.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: CustomUserDetailsService
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun successHandler(): AuthenticationSuccessHandler {
        return AuthenticationSuccessHandler { request, response, authentication ->
            val authorities = authentication.authorities.map { it.authority }
            val redirectUrl = when {
                authorities.contains("ROLE_ADMIN") -> "/admin/dashboard"
                authorities.contains("ROLE_MANAGER") -> "/manager/dashboard"
                authorities.contains("ROLE_AGENT") -> "/agent/dashboard"
                else -> "/viewer/dashboard"
            }
            response.sendRedirect(request.contextPath + redirectUrl)
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(AntPathRequestMatcher("/"), 
                                   AntPathRequestMatcher("/login"), 
                                   AntPathRequestMatcher("/css/**"), 
                                   AntPathRequestMatcher("/js/**"), 
                                   AntPathRequestMatcher("/images/**"),
                                   AntPathRequestMatcher("/h2-console/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                    .requestMatchers(AntPathRequestMatcher("/manager/**")).hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers(AntPathRequestMatcher("/agent/**")).hasAnyRole("ADMIN", "MANAGER", "AGENT")
                    .requestMatchers(AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")
                    .requestMatchers(AntPathRequestMatcher("/api/manager/**")).hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers(AntPathRequestMatcher("/api/agent/**")).hasAnyRole("ADMIN", "MANAGER", "AGENT")
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/perform_login")
                    .successHandler(successHandler())
                    .failureUrl("/login?error=true")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout=true")
                    .permitAll()
            }
            .sessionManagement { session ->
                session.maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
            }
            .csrf { csrf ->
                csrf.ignoringRequestMatchers(AntPathRequestMatcher("/h2-console/**"))
            }
            .headers { headers ->
                headers.frameOptions().sameOrigin() // Allow H2 console
            }
            .authenticationProvider(authenticationProvider())
        
        return http.build()
    }
}