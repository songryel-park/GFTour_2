package com.gftour.service

import com.gftour.entity.*
import com.gftour.repository.DocumentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 문서 서비스
 * 5개 문서 타입별 CRUD, 문서 워크플로우 관리
 */
@Service
@Transactional
class DocumentService(
    private val documentRepository: DocumentRepository
) {
    private val logger = LoggerFactory.getLogger(DocumentService::class.java)
    
    // 문서 워크플로우 순서 정의
    private val workflowOrder = listOf(
        DocumentType.QUOTATION,      // 견적서
        DocumentType.ALLOCATION,     // 수배서
        DocumentType.INVOICE,        // 청구서
        DocumentType.TOUR_CONFIRMATION, // 관광확인
        DocumentType.GUIDE_INSTRUCTION  // 가이드지침서
    )
    
    /**
     * 문서 생성
     */
    fun createDocument(
        fileRecord: FileRecord,
        documentType: DocumentType,
        title: String,
        content: String?,
        createdBy: User
    ): Document {
        logger.info("문서 생성 시작 - REF No: ${fileRecord.refNo}, 타입: $documentType")
        
        // 워크플로우 순서 검증
        validateWorkflowOrder(fileRecord, documentType)
        
        // 동일한 타입의 문서가 이미 존재하는지 확인
        val existingDocument = documentRepository.findByFileRecordAndDocumentType(fileRecord, documentType)
        if (existingDocument != null) {
            throw IllegalStateException("해당 파일에 이미 ${documentType.name} 문서가 존재합니다")
        }
        
        val document = Document(
            fileRecord = fileRecord,
            documentType = documentType,
            title = title,
            content = content,
            createdBy = createdBy
        )
        
        val saved = documentRepository.save(document)
        logger.info("문서 생성 완료 - ID: ${saved.id}, 타입: $documentType")
        
        return saved
    }
    
    /**
     * 문서 워크플로우 순서 검증
     */
    private fun validateWorkflowOrder(fileRecord: FileRecord, newDocumentType: DocumentType) {
        val existingDocuments = documentRepository.findByFileRecordOrderByWorkflow(fileRecord)
        val currentIndex = workflowOrder.indexOf(newDocumentType)
        
        // 이전 단계의 문서들이 모두 승인되었는지 확인
        for (i in 0 until currentIndex) {
            val requiredType = workflowOrder[i]
            val requiredDoc = existingDocuments.find { it.documentType == requiredType }
            
            if (requiredDoc == null || requiredDoc.status != DocumentStatus.APPROVED) {
                throw IllegalStateException("${requiredType.name} 문서가 먼저 승인되어야 합니다")
            }
        }
    }
    
    /**
     * 문서 내용 업데이트
     */
    fun updateDocument(
        id: Long,
        title: String? = null,
        content: String? = null
    ): Document {
        val existing = documentRepository.findById(id)
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다: $id") }
        
        if (existing.status == DocumentStatus.APPROVED) {
            throw IllegalStateException("승인된 문서는 수정할 수 없습니다")
        }
        
        val updated = existing.copy(
            title = title ?: existing.title,
            content = content ?: existing.content,
            version = existing.version + 1,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("문서 업데이트 완료 - ID: ${updated.id}, 버전: ${updated.version}")
        return documentRepository.save(updated)
    }
    
    /**
     * 문서 상태 변경
     */
    fun updateDocumentStatus(
        id: Long,
        status: DocumentStatus,
        approver: User? = null
    ): Document {
        val existing = documentRepository.findById(id)
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다: $id") }
        
        val updated = existing.copy(
            status = status,
            approvedBy = if (status == DocumentStatus.APPROVED) approver else existing.approvedBy,
            approvedAt = if (status == DocumentStatus.APPROVED) LocalDateTime.now() else existing.approvedAt,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("문서 상태 변경 - ID: ${updated.id}, ${existing.status} -> $status")
        return documentRepository.save(updated)
    }
    
    /**
     * 파일별 문서 목록 조회 (워크플로우 순서)
     */
    @Transactional(readOnly = true)
    fun getDocumentsByFileRecord(fileRecord: FileRecord): List<Document> {
        return documentRepository.findByFileRecordOrderByWorkflow(fileRecord)
    }
    
    /**
     * 특정 타입의 문서 조회
     */
    @Transactional(readOnly = true)
    fun getDocumentByType(fileRecord: FileRecord, documentType: DocumentType): Document? {
        return documentRepository.findByFileRecordAndDocumentType(fileRecord, documentType)
    }
    
    /**
     * 상태별 문서 목록 조회
     */
    @Transactional(readOnly = true)
    fun getDocumentsByStatus(status: DocumentStatus): List<Document> {
        return documentRepository.findByStatus(status)
    }
    
    /**
     * 템플릿 기반 문서 생성
     */
    fun createDocumentFromTemplate(
        fileRecord: FileRecord,
        documentType: DocumentType,
        createdBy: User
    ): Document {
        val (title, content) = getDocumentTemplate(documentType, fileRecord)
        
        return createDocument(
            fileRecord = fileRecord,
            documentType = documentType,
            title = title,
            content = content,
            createdBy = createdBy
        )
    }
    
    /**
     * 문서 템플릿 생성
     */
    private fun getDocumentTemplate(documentType: DocumentType, fileRecord: FileRecord): Pair<String, String> {
        val customer = fileRecord.customer
        val tour = fileRecord.tour
        
        return when (documentType) {
            DocumentType.QUOTATION -> {
                val title = "[견적서] ${customer.name} - ${tour.name}"
                val content = """
                    견적서
                    
                    고객명: ${customer.name}
                    여행상품: ${tour.name}
                    여행기간: ${fileRecord.departureDate} ~ ${fileRecord.returnDate}
                    인원: ${fileRecord.paxCount}명
                    
                    상품가격: ${tour.price}원
                    총 견적가: ${tour.price.multiply(fileRecord.paxCount.toBigDecimal())}원
                    
                    * 상기 견적은 최종 확정 시 변동될 수 있습니다.
                """.trimIndent()
                title to content
            }
            
            DocumentType.ALLOCATION -> {
                val title = "[수배서] ${customer.name} - ${tour.name}"
                val content = """
                    수배서
                    
                    REF No: ${fileRecord.refNo}
                    고객명: ${customer.name}
                    여행상품: ${tour.name}
                    여행기간: ${fileRecord.departureDate} ~ ${fileRecord.returnDate}
                    인원: ${fileRecord.paxCount}명
                    
                    담당 AGT: ${fileRecord.agt.name}
                    연락처: ${fileRecord.agt.phone}
                    
                    수배 상세 내역:
                    - 항공편:
                    - 호텔:
                    - 교통편:
                    - 가이드:
                """.trimIndent()
                title to content
            }
            
            DocumentType.INVOICE -> {
                val title = "[청구서] ${customer.name} - ${tour.name}"
                val content = """
                    청구서
                    
                    REF No: ${fileRecord.refNo}
                    고객명: ${customer.name}
                    여행상품: ${tour.name}
                    인원: ${fileRecord.paxCount}명
                    
                    청구 금액: ${tour.price.multiply(fileRecord.paxCount.toBigDecimal())}원
                    
                    결제 방법:
                    계좌번호:
                    입금 마감일:
                """.trimIndent()
                title to content
            }
            
            DocumentType.TOUR_CONFIRMATION -> {
                val title = "[관광확인] ${customer.name} - ${tour.name}"
                val content = """
                    관광확인서
                    
                    REF No: ${fileRecord.refNo}
                    고객명: ${customer.name}
                    여행상품: ${tour.name}
                    여행기간: ${fileRecord.departureDate} ~ ${fileRecord.returnDate}
                    인원: ${fileRecord.paxCount}명
                    
                    확인 사항:
                    - 항공편 확인
                    - 호텔 예약 확인
                    - 가이드 배정 확인
                    - 보험 가입 확인
                """.trimIndent()
                title to content
            }
            
            DocumentType.GUIDE_INSTRUCTION -> {
                val title = "[가이드지침서] ${customer.name} - ${tour.name}"
                val content = """
                    가이드 지침서
                    
                    REF No: ${fileRecord.refNo}
                    고객 정보: ${customer.name}
                    여행상품: ${tour.name}
                    여행기간: ${fileRecord.departureDate} ~ ${fileRecord.returnDate}
                    인원: ${fileRecord.paxCount}명
                    
                    가이드 지침:
                    1. 고객 특성 및 주의사항
                    2. 일정별 안내 포인트
                    3. 응급상황 대처 방법
                    4. 연락처 정보
                """.trimIndent()
                title to content
            }
        }
    }
    
    /**
     * 문서 삭제
     */
    fun deleteDocument(id: Long) {
        val document = documentRepository.findById(id)
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다: $id") }
        
        if (document.status == DocumentStatus.APPROVED) {
            throw IllegalStateException("승인된 문서는 삭제할 수 없습니다")
        }
        
        logger.info("문서 삭제 - ID: ${document.id}, 타입: ${document.documentType}")
        documentRepository.delete(document)
    }
}