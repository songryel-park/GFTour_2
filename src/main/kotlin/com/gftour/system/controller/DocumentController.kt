package com.gftour.system.controller

import com.gftour.system.dto.*
import com.gftour.system.entity.DocumentType
import com.gftour.system.service.DocumentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/documents")
@Tag(name = "Document", description = "문서 관리 API - 10개 문서 타입별 생성 및 관리")
class DocumentController(
    private val documentService: DocumentService
) {
    
    @PostMapping("/{type}")
    @Operation(summary = "문서 생성", description = "10개 문서 타입별 생성")
    fun createDocument(
        @PathVariable type: String,
        @Valid @RequestBody request: DocumentCreateRequest
    ): ResponseEntity<ApiResponse<DocumentDto>> {
        return try {
            val documentType = DocumentType.valueOf(type.uppercase())
            val response = documentService.createDocument(documentType, request)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "${documentType.displayName}이 성공적으로 생성되었습니다",
                    data = response
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = when {
                        e.message?.contains("Unknown enum constant") == true -> 
                            "지원하지 않는 문서 타입입니다. 지원 타입: ${DocumentType.values().joinToString(", ") { it.name.lowercase() }}"
                        else -> e.message ?: "문서 생성 실패"
                    }
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "문서 생성 실패"
                )
            )
        }
    }
    
    @GetMapping("/{fileId}/{type}")
    @Operation(summary = "문서 조회", description = "파일ID와 문서타입으로 문서 조회")
    fun getDocument(
        @PathVariable fileId: Long,
        @PathVariable type: String
    ): ResponseEntity<ApiResponse<DocumentDto?>> {
        return try {
            val documentType = DocumentType.valueOf(type.uppercase())
            val response = documentService.getDocument(fileId, documentType)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "문서 조회 완료",
                    data = response
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = "지원하지 않는 문서 타입입니다"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "문서 조회 실패"
                )
            )
        }
    }
    
    @GetMapping("/file/{fileId}")
    @Operation(summary = "파일의 모든 문서 조회", description = "특정 파일의 모든 문서 조회")
    fun getDocumentsByFileRecord(@PathVariable fileId: Long): ResponseEntity<ApiResponse<List<DocumentDto>>> {
        return try {
            val response = documentService.getDocumentsByFileRecord(fileId)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "문서 목록 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "문서 조회 실패"
                )
            )
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "문서 수정", description = "문서 내용 수정")
    fun updateDocument(
        @PathVariable id: Long,
        @Valid @RequestBody request: DocumentUpdateRequest
    ): ResponseEntity<ApiResponse<DocumentDto>> {
        return try {
            val response = documentService.updateDocument(id, request)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "문서가 성공적으로 수정되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "문서 수정 실패"
                )
            )
        }
    }
    
    @PostMapping("/{id}/approve")
    @Operation(summary = "문서 승인", description = "문서 승인 처리")
    fun approveDocument(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<DocumentDto>> {
        return try {
            val response = documentService.approveDocument(id, authentication.name)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "문서가 승인되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "문서 승인 실패"
                )
            )
        }
    }
    
    @GetMapping("/types")
    @Operation(summary = "문서 타입 목록", description = "지원하는 문서 타입 목록 조회")
    fun getDocumentTypes(): ResponseEntity<ApiResponse<List<Map<String, Any>>>> {
        val types = DocumentType.values().map { type ->
            mapOf(
                "name" to type.name.lowercase(),
                "displayName" to type.displayName,
                "order" to type.order
            )
        }.sortedBy { it["order"] as Int }
        
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                message = "문서 타입 목록 조회 완료",
                data = types
            )
        )
    }
}