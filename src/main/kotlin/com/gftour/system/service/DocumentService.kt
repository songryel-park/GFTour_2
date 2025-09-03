package com.gftour.system.service

import com.gftour.system.dto.*
import com.gftour.system.entity.*
import com.gftour.system.repository.DocumentRepository
import com.gftour.system.repository.FileRecordRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DocumentService(
    private val documentRepository: DocumentRepository,
    private val fileRecordRepository: FileRecordRepository
) {
    
    fun createDocument(type: DocumentType, request: DocumentCreateRequest): DocumentDto {
        val fileRecord = fileRecordRepository.findById(request.fileRecordId)
            .orElseThrow { IllegalArgumentException("파일 레코드를 찾을 수 없습니다") }
        
        // Check workflow validation
        validateDocumentWorkflow(fileRecord, type)
        
        // Check if document already exists for this type
        val existingDocument = documentRepository.findByFileRecordAndType(fileRecord, type)
        if (existingDocument != null) {
            throw IllegalArgumentException("${type.displayName}이 이미 존재합니다")
        }
        
        val document = Document(
            fileRecord = fileRecord,
            type = type,
            content = request.content,
            amount = request.amount,
            status = DocumentStatus.DRAFT
        )
        
        val savedDocument = documentRepository.save(document)
        return toDto(savedDocument)
    }
    
    fun updateDocument(id: Long, request: DocumentUpdateRequest): DocumentDto {
        val document = documentRepository.findById(id)
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다") }
        
        val updatedDocument = document.copy(
            content = request.content ?: document.content,
            amount = request.amount ?: document.amount,
            status = request.status ?: document.status,
            updatedAt = LocalDateTime.now()
        )
        
        val savedDocument = documentRepository.save(updatedDocument)
        return toDto(savedDocument)
    }
    
    fun getDocument(fileId: Long, type: DocumentType): DocumentDto? {
        val fileRecord = fileRecordRepository.findById(fileId)
            .orElseThrow { IllegalArgumentException("파일 레코드를 찾을 수 없습니다") }
        
        val document = documentRepository.findByFileRecordAndType(fileRecord, type)
        return document?.let { toDto(it) }
    }
    
    fun getDocumentById(id: Long): DocumentDto {
        val document = documentRepository.findById(id)
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다") }
        return toDto(document)
    }
    
    fun getDocumentsByFileRecord(fileRecordId: Long): List<DocumentDto> {
        val documents = documentRepository.findByFileRecordId(fileRecordId)
        return documents.map { toDto(it) }
    }
    
    fun approveDocument(id: Long, approvedBy: String): DocumentDto {
        val document = documentRepository.findById(id)
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다") }
        
        val approvedDocument = document.copy(
            status = DocumentStatus.APPROVED,
            approvedBy = approvedBy,
            approvedAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        val savedDocument = documentRepository.save(approvedDocument)
        return toDto(savedDocument)
    }
    
    private fun validateDocumentWorkflow(fileRecord: FileRecord, requestedType: DocumentType) {
        val existingDocuments = documentRepository.findByFileRecord(fileRecord)
        val approvedTypes = existingDocuments
            .filter { it.status == DocumentStatus.APPROVED }
            .map { it.type.order }
            .toSet()
        
        // Workflow: 견적서(1) → 수배서(2) → 청구서(3) → 고객확인(4) → 가이드지침서(5)
        when (requestedType.order) {
            1 -> return // 견적서는 언제나 생성 가능
            2 -> { // 수배서는 견적서가 승인되어야 함
                if (!approvedTypes.contains(1)) {
                    throw IllegalArgumentException("견적서가 먼저 승인되어야 합니다")
                }
            }
            3 -> { // 청구서는 수배서가 승인되어야 함
                if (!approvedTypes.contains(2)) {
                    throw IllegalArgumentException("수배서가 먼저 승인되어야 합니다")
                }
            }
            4 -> { // 고객확인은 청구서가 승인되어야 함
                if (!approvedTypes.contains(3)) {
                    throw IllegalArgumentException("청구서가 먼저 승인되어야 합니다")
                }
            }
            5 -> { // 가이드지침서는 고객확인이 승인되어야 함
                if (!approvedTypes.contains(4)) {
                    throw IllegalArgumentException("고객확인이 먼저 승인되어야 합니다")
                }
            }
            // 기타 문서들(6-10)은 워크플로우 제약이 없음
        }
    }
    
    private fun toDto(document: Document): DocumentDto {
        return DocumentDto(
            id = document.id,
            fileRecordId = document.fileRecord?.id ?: 0L,
            type = document.type,
            status = document.status,
            content = document.content,
            amount = document.amount,
            approvedBy = document.approvedBy,
            approvedAt = document.approvedAt,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt
        )
    }
}