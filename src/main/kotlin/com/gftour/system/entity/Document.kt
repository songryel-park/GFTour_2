package com.gftour.system.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "documents")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_record_id", nullable = false)
    val fileRecord: FileRecord? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: DocumentType = DocumentType.QUOTE,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: DocumentStatus = DocumentStatus.DRAFT,
    
    @Column(columnDefinition = "TEXT")
    val content: String? = null,
    
    @Column(name = "amount", precision = 12, scale = 2)
    val amount: BigDecimal? = null,
    
    @Column(name = "approved_by", length = 50)
    val approvedBy: String? = null,
    
    @Column(name = "approved_at")
    val approvedAt: LocalDateTime? = null,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class DocumentType(val displayName: String, val order: Int) {
    QUOTE("견적서", 1),
    BOOKING("수배서", 2),
    INVOICE("청구서", 3),
    CUSTOMER_CONFIRMATION("고객확인", 4),
    GUIDE_INSTRUCTION("가이드지침서", 5),
    HOTEL_OTHERS("호텔&기타", 6),
    FINAL("Final", 7),
    COMMISSION("수수료", 8),
    TOUR_SCHEDULE_APPROVAL("관광일정승인", 9),
    TOUR_CONFIRMATION_APPROVAL("관광확인승인", 10)
}

enum class DocumentStatus {
    DRAFT,      // 초안
    SUBMITTED,  // 제출됨
    APPROVED,   // 승인됨
    REJECTED    // 거절됨
}