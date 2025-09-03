package com.gftour.system.exception

import com.gftour.system.dto.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.badRequest().body(
            ApiResponse<Nothing>(
                success = false,
                message = e.message ?: "잘못된 요청입니다"
            )
        )
    }
    
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiResponse<Nothing>(
                success = false,
                message = "이메일 또는 비밀번호가 잘못되었습니다"
            )
        )
    }
    
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String>>> {
        val errors = e.bindingResult.fieldErrors.associate { error ->
            error.field to (error.defaultMessage ?: "유효하지 않은 값입니다")
        }
        
        return ResponseEntity.badRequest().body(
            ApiResponse(
                success = false,
                message = "입력 값 검증 실패",
                data = errors
            )
        )
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Nothing>> {
        val message = when (e.name) {
            "status" -> "상태 값이 올바르지 않습니다"
            "type" -> "타입 값이 올바르지 않습니다"
            else -> "파라미터 타입이 올바르지 않습니다: ${e.name}"
        }
        
        return ResponseEntity.badRequest().body(
            ApiResponse<Nothing>(
                success = false,
                message = message
            )
        )
    }
    
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(e: NoSuchElementException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse<Nothing>(
                success = false,
                message = "요청한 리소스를 찾을 수 없습니다"
            )
        )
    }
    
    @ExceptionHandler(SecurityException::class)
    fun handleSecurityException(e: SecurityException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ApiResponse<Nothing>(
                success = false,
                message = "권한이 없습니다"
            )
        )
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        // 프로덕션에서는 상세 에러 메시지를 숨기고 로깅만 수행
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse<Nothing>(
                success = false,
                message = "서버 내부 오류가 발생했습니다"
            )
        )
    }
}