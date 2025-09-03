package com.gftour.system.service

import com.gftour.system.config.JwtUtil
import com.gftour.system.dto.AuthResponse
import com.gftour.system.dto.LoginRequest
import com.gftour.system.dto.RegisterRequest
import com.gftour.system.dto.UserDto
import com.gftour.system.entity.User
import com.gftour.system.entity.UserRole
import com.gftour.system.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {
    
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("이미 등록된 이메일입니다")
        }
        
        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name,
            role = UserRole.USER
        )
        
        val savedUser = userRepository.save(user)
        
        val token = jwtUtil.generateToken(savedUser)
        val refreshToken = jwtUtil.generateRefreshToken(savedUser)
        
        return AuthResponse(
            token = token,
            refreshToken = refreshToken,
            user = UserDto(
                id = savedUser.id,
                email = savedUser.email,
                name = savedUser.name,
                role = savedUser.role.name
            )
        )
    }
    
    fun login(request: LoginRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )
        
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다") }
        
        val token = jwtUtil.generateToken(user)
        val refreshToken = jwtUtil.generateRefreshToken(user)
        
        return AuthResponse(
            token = token,
            refreshToken = refreshToken,
            user = UserDto(
                id = user.id,
                email = user.email,
                name = user.name,
                role = user.role.name
            )
        )
    }
    
    fun refreshToken(refreshToken: String): String {
        if (!jwtUtil.isValidToken(refreshToken)) {
            throw IllegalArgumentException("유효하지 않은 리프레시 토큰입니다")
        }
        
        val username = jwtUtil.extractUsername(refreshToken)
        val user = userRepository.findByEmail(username)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다") }
        
        return jwtUtil.generateToken(user)
    }
}