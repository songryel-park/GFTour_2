package com.gftour.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "documents")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val title: String,
    
    @Column(columnDefinition = "TEXT")
    val content: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: DocumentType,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: DocumentStatus = DocumentStatus.DRAFT,
    
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    val createdBy: User,
    
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    val assignedTo: User? = null,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val updatedAt: LocalDateTime? = null,
    
    val approvedAt: LocalDateTime? = null,
    
    @ManyToOne
    @JoinColumn(name = "approved_by")
    val approvedBy: User? = null,
    
    // Financial fields for tour packages, invoices, etc.
    @Column
    val totalAmount: BigDecimal? = null,
    
    @Column
    val paidAmount: BigDecimal? = null,
    
    @Column
    val customerName: String? = null,
    
    @Column
    val customerEmail: String? = null,
    
    @Column
    val customerPhone: String? = null,
    
    // Tour specific fields
    @Column
    val tourCode: String? = null,
    
    @Column
    val destination: String? = null,
    
    val startDate: LocalDateTime? = null,
    
    val endDate: LocalDateTime? = null,
    
    @Column
    val numberOfPassengers: Int? = null
)

enum class DocumentType {
    TOUR_PACKAGE,
    INVOICE,
    QUOTATION,
    BOOKING_CONFIRMATION,
    ITINERARY,
    EXPENSE_REPORT,
    COMMISSION_REPORT,
    CUSTOMER_FEEDBACK,
    CONTRACT,
    OTHER
}

enum class DocumentStatus {
    DRAFT,
    PENDING_REVIEW,
    APPROVED,
    REJECTED,
    ARCHIVED
}