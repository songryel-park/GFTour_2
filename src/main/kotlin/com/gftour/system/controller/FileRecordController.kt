package com.gftour.system.controller

import com.gftour.system.dto.*
import com.gftour.system.service.FileRecordService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/files")
@Tag(name = "File Record", description = "파일 관리 API - 신규등록, 검색, CRUD")
class FileRecordController(
    private val fileRecordService: FileRecordService
) {
    
    @PostMapping
    @Operation(summary = "신규등록", description = "새로운 파일 레코드 등록")
    fun createFile(
        @Valid @RequestBody request: FileRecordCreateRequest,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<FileRecordDto>> {
        return try {
            val response = fileRecordService.createFileRecord(request, authentication.name)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "파일이 성공적으로 등록되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "파일 등록 실패"
                )
            )
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "파일 검색", description = "조건에 따른 파일 검색")
    fun searchFiles(
        @RequestParam(required = false) refNo: String?,
        @RequestParam(required = false) destination: String?,
        @RequestParam(required = false) manager: String?,
        @RequestParam(required = false) status: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<FileRecordDto>>> {
        return try {
            val searchRequest = FileSearchRequest(
                refNo = refNo,
                destination = destination,
                manager = manager,
                status = status?.let { com.gftour.system.entity.FileStatus.valueOf(it.uppercase()) }
            )
            
            val response = fileRecordService.searchFiles(searchRequest, page, size)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "검색 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "검색 실패"
                )
            )
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "파일 조회", description = "ID로 파일 레코드 조회")
    fun getFile(@PathVariable id: Long): ResponseEntity<ApiResponse<FileRecordDto>> {
        return try {
            val response = fileRecordService.getFileRecord(id)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "파일 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "파일 조회 실패"
                )
            )
        }
    }
    
    @GetMapping("/ref/{refNo}")
    @Operation(summary = "REF No로 파일 조회", description = "REF No로 파일 레코드 조회")
    fun getFileByRefNo(@PathVariable refNo: String): ResponseEntity<ApiResponse<FileRecordDto?>> {
        return try {
            val response = fileRecordService.getFileRecordByRefNo(refNo)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "파일 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "파일 조회 실패"
                )
            )
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "파일 수정", description = "파일 정보 수정")
    fun updateFile(
        @PathVariable id: Long,
        @Valid @RequestBody request: FileRecordUpdateRequest
    ): ResponseEntity<ApiResponse<FileRecordDto>> {
        return try {
            val response = fileRecordService.updateFileRecord(id, request)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "파일이 성공적으로 수정되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "파일 수정 실패"
                )
            )
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "파일 삭제", description = "파일 삭제")
    fun deleteFile(@PathVariable id: Long): ResponseEntity<ApiResponse<String>> {
        return try {
            fileRecordService.deleteFileRecord(id)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "파일이 성공적으로 삭제되었습니다",
                    data = "삭제 완료"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "파일 삭제 실패"
                )
            )
        }
    }
    
    @PostMapping("/{id}/save")
    @Operation(summary = "파일 저장", description = "Save 버튼 기능 - 파일 상태를 진행중으로 변경")
    fun saveFile(@PathVariable id: Long): ResponseEntity<ApiResponse<FileRecordDto>> {
        return try {
            val response = fileRecordService.saveFile(id)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "파일이 저장되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "파일 저장 실패"
                )
            )
        }
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "파일 취소", description = "Cancel 버튼 기능 - 파일 상태를 취소로 변경")
    fun cancelFile(@PathVariable id: Long): ResponseEntity<ApiResponse<FileRecordDto>> {
        return try {
            val response = fileRecordService.cancelFile(id)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "파일이 취소되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "파일 취소 실패"
                )
            )
        }
    }
}