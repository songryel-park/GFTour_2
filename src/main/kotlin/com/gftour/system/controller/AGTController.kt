package com.gftour.system.controller

import com.gftour.system.dto.*
import com.gftour.system.service.AGTService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/agts")
@Tag(name = "AGT", description = "AGT 정보 관리 API")
class AGTController(
    private val agtService: AGTService
) {
    
    @PostMapping
    @Operation(summary = "신규 AGT 등록", description = "새로운 AGT 등록")
    fun createAGT(@Valid @RequestBody request: AGTCreateRequest): ResponseEntity<ApiResponse<AGTDto>> {
        return try {
            val response = agtService.createAGT(request)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "AGT가 성공적으로 등록되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "AGT 등록 실패"
                )
            )
        }
    }

    @GetMapping
    @Operation(summary = "AGT 목록 조회", description = "모든 AGT 목록 조회")
    fun getAllAGTs(): ResponseEntity<ApiResponse<List<AGTDto>>> {
        return try {
            val response = agtService.getAllAGTs()
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "AGT 목록 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "AGT 목록 조회 실패"
                )
            )
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "AGT 상세 조회", description = "특정 AGT의 상세 정보 조회")
    fun getAGT(@PathVariable id: Long): ResponseEntity<ApiResponse<AGTDto>> {
        return try {
            val response = agtService.getAGT(id)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "AGT 정보 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "AGT 정보 조회 실패"
                )
            )
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "AGT 정보 수정", description = "AGT 정보 수정")
    fun updateAGT(
        @PathVariable id: Long,
        @Valid @RequestBody request: AGTUpdateRequest
    ): ResponseEntity<ApiResponse<AGTDto>> {
        return try {
            val response = agtService.updateAGT(id, request)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "AGT 정보가 성공적으로 수정되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "AGT 정보 수정 실패"
                )
            )
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "AGT 삭제", description = "AGT 정보 삭제")
    fun deleteAGT(@PathVariable id: Long): ResponseEntity<ApiResponse<String>> {
        return try {
            agtService.deleteAGT(id)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "AGT가 성공적으로 삭제되었습니다",
                    data = "삭제 완료"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "AGT 삭제 실패"
                )
            )
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "AGT 검색", description = "이름, 지역, 국가로 AGT 검색")
    fun searchAGTs(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) region: String?,
        @RequestParam(required = false) country: String?
    ): ResponseEntity<ApiResponse<List<AGTDto>>> {
        return try {
            val response = agtService.searchAGTs(name, region, country)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "AGT 검색 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "AGT 검색 실패"
                )
            )
        }
    }
}