package com.gftour.service

import com.gftour.entity.*
import com.gftour.repository.DocumentRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

/**
 * DocumentService 테스트
 */
@SpringJUnitConfig
class DocumentServiceTest {
    
    private val documentRepository = mockk<DocumentRepository>()
    private val documentService = DocumentService(documentRepository)
    
    @Test
    fun `문서 생성 테스트`() {
        // Given
        val fileRecord = mockk<FileRecord>()
        val user = mockk<User>()
        val document = mockk<Document>()
        
        every { fileRecord.refNo } returns "GF-20240101-001"
        every { documentRepository.findByFileRecordOrderByWorkflow(fileRecord) } returns emptyList()
        every { documentRepository.findByFileRecordAndDocumentType(fileRecord, DocumentType.QUOTATION) } returns null
        every { documentRepository.save(any()) } returns document
        every { document.id } returns 1L
        
        // When
        val result = documentService.createDocument(
            fileRecord = fileRecord,
            documentType = DocumentType.QUOTATION,
            title = "테스트 견적서",
            content = "테스트 내용",
            createdBy = user
        )
        
        // Then
        assert(result == document)
        verify { documentRepository.save(any()) }
    }
    
    @Test
    fun `중복 문서 생성 시 예외 발생 테스트`() {
        // Given
        val fileRecord = mockk<FileRecord>()
        val user = mockk<User>()
        val existingDocument = mockk<Document>()
        
        every { documentRepository.findByFileRecordOrderByWorkflow(fileRecord) } returns emptyList()
        every { documentRepository.findByFileRecordAndDocumentType(fileRecord, DocumentType.QUOTATION) } returns existingDocument
        
        // When & Then
        assertThrows<IllegalStateException> {
            documentService.createDocument(
                fileRecord = fileRecord,
                documentType = DocumentType.QUOTATION,
                title = "테스트 견적서",
                content = "테스트 내용",
                createdBy = user
            )
        }
    }
    
    @Test
    fun `문서 워크플로우 순서 검증 테스트`() {
        // Given
        val fileRecord = mockk<FileRecord>()
        val user = mockk<User>()
        
        // 견적서가 없는 상태에서 수배서 생성 시도
        every { documentRepository.findByFileRecordOrderByWorkflow(fileRecord) } returns emptyList()
        every { documentRepository.findByFileRecordAndDocumentType(fileRecord, DocumentType.ALLOCATION) } returns null
        
        // When & Then
        assertThrows<IllegalStateException> {
            documentService.createDocument(
                fileRecord = fileRecord,
                documentType = DocumentType.ALLOCATION,
                title = "테스트 수배서",
                content = "테스트 내용",
                createdBy = user
            )
        }
    }
}