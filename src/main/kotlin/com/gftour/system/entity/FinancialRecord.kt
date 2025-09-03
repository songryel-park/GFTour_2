package com.gftour.system.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "financial_records")
data class FinancialRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_record_id", nullable = false)
    val fileRecord: FileRecord? = null,
    
    @Column(name = "receipt_amount", precision = 12, scale = 2, nullable = false)
    val receiptAmount: BigDecimal = BigDecimal.ZERO,
    
    @Column(name = "sales_amount", precision = 12, scale = 2, nullable = false)
    val salesAmount: BigDecimal = BigDecimal.ZERO,
    
    @Column(name = "operating_cost", precision = 12, scale = 2, nullable = false)
    val operatingCost: BigDecimal = BigDecimal.ZERO,
    
    @Column(name = "sub_total", precision = 12, scale = 2, nullable = false)
    val subTotal: BigDecimal = BigDecimal.ZERO,
    
    @Column(name = "commission_rate", precision = 5, scale = 2)
    val commissionRate: BigDecimal? = null,
    
    @Column(name = "commission_amount", precision = 12, scale = 2)
    val commissionAmount: BigDecimal? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: FinancialStatus = FinancialStatus.DRAFT,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // 자동 계산: Sub Total = 판매송금합계 - 수취송금합계 - 운용비용합계
    fun calculateSubTotal(): BigDecimal {
        return salesAmount.subtract(receiptAmount).subtract(operatingCost)
    }
}

enum class FinancialStatus {
    DRAFT,      // 초안
    CALCULATED, // 계산됨
    APPROVED,   // 승인됨
    FINALIZED   // 확정됨
}