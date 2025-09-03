package com.gftour.controller

import com.gftour.entity.*
import com.gftour.service.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 문서 관리 REST 컨트롤러
 * 5개 문서 타입 관리 API
 */
@RestController
@RequestMapping("/api/documents")
class DocumentController(
    private val documentService: DocumentService,
    private val fileRecordService: FileRecordService
) {
    
    /**
     * 문서 생성
     */
    @PostMapping
    fun createDocument(@RequestBody request: CreateDocumentRequest): ResponseEntity<Document> {
        val fileRecord = fileRecordService.findByRefNo(request.refNo)
            ?: return ResponseEntity.badRequest().build()
        
        val user = User(id = request.createdBy, username = "system", password = "", email = "")
        
        val document = documentService.createDocument(
            fileRecord = fileRecord,
            documentType = request.documentType,
            title = request.title,
            content = request.content,
            createdBy = user
        )
        
        return ResponseEntity.ok(document)
    }
    
    /**
     * 템플릿 기반 문서 생성
     */
    @PostMapping("/from-template")
    fun createDocumentFromTemplate(@RequestBody request: CreateDocumentFromTemplateRequest): ResponseEntity<Document> {
        val fileRecord = fileRecordService.findByRefNo(request.refNo)
            ?: return ResponseEntity.badRequest().build()
        
        val user = User(id = request.createdBy, username = "system", password = "", email = "")
        
        val document = documentService.createDocumentFromTemplate(
            fileRecord = fileRecord,
            documentType = request.documentType,
            createdBy = user
        )
        
        return ResponseEntity.ok(document)
    }
    
    /**
     * 파일별 문서 목록 조회
     */
    @GetMapping("/by-ref-no/{refNo}")
    fun getDocumentsByRefNo(@PathVariable refNo: String): ResponseEntity<List<Document>> {
        val fileRecord = fileRecordService.findByRefNo(refNo)
            ?: return ResponseEntity.notFound().build()
        
        val documents = documentService.getDocumentsByFileRecord(fileRecord)
        return ResponseEntity.ok(documents)
    }
    
    /**
     * 문서 상태 변경
     */
    @PutMapping("/{id}/status")
    fun updateDocumentStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateDocumentStatusRequest
    ): ResponseEntity<Document> {
        val user = if (request.approverId != null) {
            User(id = request.approverId, username = "approver", password = "", email = "")
        } else null
        
        val updated = documentService.updateDocumentStatus(id, request.status, user)
        return ResponseEntity.ok(updated)
    }
    
    /**
     * 상태별 문서 조회
     */
    @GetMapping("/by-status/{status}")
    fun getDocumentsByStatus(@PathVariable status: DocumentStatus): ResponseEntity<List<Document>> {
        val documents = documentService.getDocumentsByStatus(status)
        return ResponseEntity.ok(documents)
    }
}

/**
 * 문서 생성 요청
 */
data class CreateDocumentRequest(
    val refNo: String,
    val documentType: DocumentType,
    val title: String,
    val content: String?,
    val createdBy: Long
)

/**
 * 템플릿 기반 문서 생성 요청
 */
data class CreateDocumentFromTemplateRequest(
    val refNo: String,
    val documentType: DocumentType,
    val createdBy: Long
)

/**
 * 문서 상태 변경 요청
 */
data class UpdateDocumentStatusRequest(
    val status: DocumentStatus,
    val approverId: Long? = null
)