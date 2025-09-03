package com.gftour.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * 문서 엔티티
 * 5개 문서 타입 관리 (견적서, 수배서, 청구서, 관광확인, 가이드지침서)
 */
@Entity
@Table(name = "documents")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_record_id")
    val fileRecord: FileRecord,
    
    @Enumerated(EnumType.STRING)
    val documentType: DocumentType,
    
    @NotBlank(message = "문서 제목은 필수입니다")
    @Size(max = 200)
    val title: String,
    
    @Column(columnDefinition = "TEXT")
    val content: String? = null,
    
    @Enumerated(EnumType.STRING)
    val status: DocumentStatus = DocumentStatus.DRAFT,
    
    val version: Int = 1,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    val createdBy: User,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    val approvedBy: User? = null,
    
    val approvedAt: LocalDateTime? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class DocumentType {
    QUOTATION,      // 견적서
    ALLOCATION,     // 수배서  
    INVOICE,        // 청구서
    TOUR_CONFIRMATION, // 관광확인
    GUIDE_INSTRUCTION  // 가이드지침서
}

enum class DocumentStatus {
    DRAFT,          // 작성중
    PENDING_APPROVAL, // 승인대기
    APPROVED,       // 승인완료
    REJECTED        // 거부됨
}