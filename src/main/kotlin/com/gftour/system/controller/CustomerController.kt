package com.gftour.system.controller

import com.gftour.system.dto.*
import com.gftour.system.service.CustomerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/customers")
@Tag(name = "Customer", description = "고객명단 관리 API - 이름, 나이, 여권번호")
class CustomerController(
    private val customerService: CustomerService
) {
    
    @PostMapping
    @Operation(summary = "신규 고객 등록", description = "새로운 고객 등록")
    fun createCustomer(@Valid @RequestBody request: CustomerCreateRequest): ResponseEntity<ApiResponse<CustomerDto>> {
        return try {
            val response = customerService.createCustomer(request)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "고객이 성공적으로 등록되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "고객 등록 실패"
                )
            )
        }
    }
    
    @GetMapping
    @Operation(summary = "고객명단 조회", description = "모든 고객 목록 조회")
    fun getAllCustomers(): ResponseEntity<ApiResponse<List<CustomerDto>>> {
        return try {
            val response = customerService.getAllCustomers()
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "고객명단 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "고객명단 조회 실패"
                )
            )
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "고객 상세 정보", description = "특정 고객의 상세 정보 조회")
    fun getCustomer(@PathVariable id: Long): ResponseEntity<ApiResponse<CustomerDto>> {
        return try {
            val response = customerService.getCustomer(id)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "고객 정보 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "고객 정보 조회 실패"
                )
            )
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "고객 정보 수정", description = "고객 정보 수정")
    fun updateCustomer(
        @PathVariable id: Long,
        @Valid @RequestBody request: CustomerUpdateRequest
    ): ResponseEntity<ApiResponse<CustomerDto>> {
        return try {
            val response = customerService.updateCustomer(id, request)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "고객 정보가 성공적으로 수정되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "고객 정보 수정 실패"
                )
            )
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "고객 삭제", description = "고객 정보 삭제")
    fun deleteCustomer(@PathVariable id: Long): ResponseEntity<ApiResponse<String>> {
        return try {
            customerService.deleteCustomer(id)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "고객 정보가 성공적으로 삭제되었습니다",
                    data = "삭제 완료"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "고객 정보 삭제 실패"
                )
            )
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "고객 검색", description = "이름 또는 여권번호로 고객 검색")
    fun searchCustomers(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) passportNumber: String?
    ): ResponseEntity<ApiResponse<List<CustomerDto>>> {
        return try {
            val response = customerService.searchCustomers(name, passportNumber)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "고객 검색 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "고객 검색 실패"
                )
            )
        }
    }
    
    @GetMapping("/{id}/history")
    @Operation(summary = "고객별 여행 이력", description = "고객의 과거 여행 이력 조회")
    fun getCustomerTravelHistory(@PathVariable id: Long): ResponseEntity<ApiResponse<List<CustomerDto>>> {
        return try {
            val response = customerService.getCustomerTravelHistory(id)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "고객 여행 이력 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "여행 이력 조회 실패"
                )
            )
        }
    }
    
    @GetMapping("/file/{fileId}")
    @Operation(summary = "파일별 고객 목록", description = "특정 파일의 고객 목록 조회")
    fun getCustomersByFile(@PathVariable fileId: Long): ResponseEntity<ApiResponse<List<CustomerDto>>> {
        return try {
            val response = customerService.getCustomersByFileRecord(fileId)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "파일별 고객 목록 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "고객 목록 조회 실패"
                )
            )
        }
    }
}