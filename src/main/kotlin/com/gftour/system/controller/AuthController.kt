package com.gftour.system.controller

import com.gftour.system.dto.*
import com.gftour.system.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "인증 관련 API")
class AuthController(
    private val authService: AuthService
) {
    
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 로그인")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val response = authService.login(request)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "로그인 성공",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "로그인 실패"
                )
            )
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "신규 사용자 등록")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val response = authService.register(request)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "회원가입 성공",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "회원가입 실패"
                )
            )
        }
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "JWT 토큰 갱신")
    fun refreshToken(@RequestBody refreshTokenRequest: Map<String, String>): ResponseEntity<ApiResponse<String>> {
        val refreshToken = refreshTokenRequest["refreshToken"] 
            ?: return ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = "리프레시 토큰이 필요합니다"
                )
            )
        
        return try {
            val newToken = authService.refreshToken(refreshToken)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "토큰 갱신 성공",
                    data = newToken
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "토큰 갱신 실패"
                )
            )
        }
    }
    
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃")
    fun logout(): ResponseEntity<ApiResponse<String>> {
        // JWT는 stateless이므로 클라이언트에서 토큰을 삭제하면 됨
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "로그아웃 성공",
                data = "로그아웃되었습니다"
            )
        )
    }
}