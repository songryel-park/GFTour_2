package com.gftour.entity

import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 재무 기록 엔티티
 * 수취송금, 판매송금, 운용비용 관리 및 계산
 */
@Entity
@Table(name = "financial_records")
data class FinancialRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_record_id")
    val fileRecord: FileRecord,
    
    // 수취송금합계(W)
    @DecimalMin(value = "0.0", message = "수취송금합계는 0 이상이어야 합니다")
    val receivedAmount: BigDecimal = BigDecimal.ZERO,
    
    // 판매송금합계(W)  
    @DecimalMin(value = "0.0", message = "판매송금합계는 0 이상이어야 합니다")
    val salesAmount: BigDecimal = BigDecimal.ZERO,
    
    // 운용비용합계(W)
    @DecimalMin(value = "0.0", message = "운용비용합계는 0 이상이어야 합니다")
    val operatingCost: BigDecimal = BigDecimal.ZERO,
    
    // Sub Total 자동 계산
    val subTotal: BigDecimal = BigDecimal.ZERO,
    
    // 수수료
    val commission: BigDecimal = BigDecimal.ZERO,
    
    // 미지급액
    val unpaidAmount: BigDecimal = BigDecimal.ZERO,
    
    @Enumerated(EnumType.STRING)
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    
    val notes: String? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Sub Total 계산: 수취송금 - 판매송금 - 운용비용
     */
    fun calculateSubTotal(): BigDecimal {
        return receivedAmount.subtract(salesAmount).subtract(operatingCost)
    }
    
    /**
     * 실제 미지급액 계산
     */
    fun calculateUnpaidAmount(): BigDecimal {
        return subTotal.subtract(commission)
    }
}

enum class PaymentStatus {
    PENDING,    // 대기중
    PARTIAL,    // 부분지급
    COMPLETED,  // 완료
    OVERDUE     // 연체
}