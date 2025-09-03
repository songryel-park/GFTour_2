package com.gftour.system.controller

import com.gftour.system.dto.*
import com.gftour.system.entity.GuideStatus
import com.gftour.system.service.GuideService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/guides")
@Tag(name = "Guide", description = "가이드 관리 API - 가이드 지침서, 단체행동계획서")
class GuideController(
    private val guideService: GuideService
) {
    
    @PostMapping
    @Operation(summary = "가이드 정보 등록/수정", description = "가이드 지침서 등록 또는 수정")
    fun createOrUpdateGuide(@Valid @RequestBody request: GuideCreateRequest): ResponseEntity<ApiResponse<GuideDto>> {
        return try {
            val response = guideService.createOrUpdateGuide(request)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "가이드 정보가 성공적으로 저장되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "가이드 정보 저장 실패"
                )
            )
        }
    }
    
    @GetMapping
    @Operation(summary = "가이드 지침서 목록", description = "모든 가이드 지침서 목록 조회")
    fun getAllGuides(): ResponseEntity<ApiResponse<List<GuideDto>>> {
        return try {
            val response = guideService.getAllGuides()
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "가이드 목록 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "가이드 목록 조회 실패"
                )
            )
        }
    }
    
    @GetMapping("/{fileId}")
    @Operation(summary = "특정 파일의 가이드 지침서", description = "파일 ID로 가이드 지침서 조회")
    fun getGuideByFileId(@PathVariable fileId: Long): ResponseEntity<ApiResponse<GuideDto?>> {
        return try {
            val response = guideService.getGuide(fileId)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "가이드 지침서 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "가이드 지침서 조회 실패"
                )
            )
        }
    }
    
    @GetMapping("/by-guide/{guideName}")
    @Operation(summary = "가이드명으로 검색", description = "가이드 이름으로 지침서 검색")
    fun getGuidesByName(@PathVariable guideName: String): ResponseEntity<ApiResponse<List<GuideDto>>> {
        return try {
            val response = guideService.getGuidesByGuideName(guideName)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "가이드 검색 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "가이드 검색 실패"
                )
            )
        }
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "가이드 상태 변경", description = "가이드 지침서 상태 변경")
    fun updateGuideStatus(
        @PathVariable id: Long,
        @RequestParam status: String
    ): ResponseEntity<ApiResponse<GuideDto>> {
        return try {
            val guideStatus = GuideStatus.valueOf(status.uppercase())
            val response = guideService.updateGuideStatus(id, guideStatus)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "가이드 상태가 변경되었습니다",
                    data = response
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = "지원하지 않는 상태입니다. 지원 상태: ${GuideStatus.values().joinToString(", ") { it.name.lowercase() }}"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "가이드 상태 변경 실패"
                )
            )
        }
    }
    
    @PostMapping("/{fileId}/action-plan")
    @Operation(summary = "단체행동계획서 생성", description = "단체행동계획서 작성 및 업데이트")
    fun createGroupActionPlan(
        @PathVariable fileId: Long,
        @RequestBody planRequest: Map<String, String>
    ): ResponseEntity<ApiResponse<GuideDto>> {
        val plan = planRequest["plan"] ?: return ResponseEntity.badRequest().body(
            ApiResponse(
                success = false,
                message = "단체행동계획서 내용이 필요합니다"
            )
        )
        
        return try {
            val response = guideService.createGroupActionPlan(fileId, plan)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "단체행동계획서가 생성되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "단체행동계획서 생성 실패"
                )
            )
        }
    }
    
    @GetMapping("/statuses")
    @Operation(summary = "가이드 상태 목록", description = "지원하는 가이드 상태 목록 조회")
    fun getGuideStatuses(): ResponseEntity<ApiResponse<List<Map<String, String>>>> {
        val statuses = GuideStatus.values().map { status ->
            mapOf(
                "name" to status.name.lowercase(),
                "displayName" to when (status) {
                    GuideStatus.DRAFT -> "초안"
                    GuideStatus.PREPARED -> "준비됨"
                    GuideStatus.APPROVED -> "승인됨"
                    GuideStatus.COMPLETED -> "완료됨"
                }
            )
        }
        
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "가이드 상태 목록 조회 완료",
                data = statuses
            )
        )
    }
}