package com.gftour.service

import com.gftour.entity.*
import com.gftour.repository.FinancialRecordRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

/**
 * 재무 서비스
 * 수취송금, 판매송금, 운용비용 관리 및 계산
 */
@Service
@Transactional
class FinancialService(
    private val financialRecordRepository: FinancialRecordRepository
) {
    private val logger = LoggerFactory.getLogger(FinancialService::class.java)
    
    /**
     * 재무 기록 생성
     */
    fun createFinancialRecord(
        fileRecord: FileRecord,
        receivedAmount: BigDecimal = BigDecimal.ZERO,
        salesAmount: BigDecimal = BigDecimal.ZERO,
        operatingCost: BigDecimal = BigDecimal.ZERO,
        commission: BigDecimal = BigDecimal.ZERO,
        notes: String? = null
    ): FinancialRecord {
        logger.info("재무 기록 생성 시작 - REF No: ${fileRecord.refNo}")
        
        // 이미 재무 기록이 존재하는지 확인
        val existing = financialRecordRepository.findByFileRecord(fileRecord)
        if (existing != null) {
            throw IllegalStateException("해당 파일에 이미 재무 기록이 존재합니다")
        }
        
        val subTotal = calculateSubTotal(receivedAmount, salesAmount, operatingCost)
        val unpaidAmount = calculateUnpaidAmount(subTotal, commission)
        
        val financialRecord = FinancialRecord(
            fileRecord = fileRecord,
            receivedAmount = receivedAmount,
            salesAmount = salesAmount,
            operatingCost = operatingCost,
            subTotal = subTotal,
            commission = commission,
            unpaidAmount = unpaidAmount,
            notes = notes
        )
        
        val saved = financialRecordRepository.save(financialRecord)
        logger.info("재무 기록 생성 완료 - ID: ${saved.id}, SubTotal: ${saved.subTotal}")
        
        return saved
    }
    
    /**
     * 재무 기록 업데이트
     */
    fun updateFinancialRecord(
        id: Long,
        receivedAmount: BigDecimal? = null,
        salesAmount: BigDecimal? = null,
        operatingCost: BigDecimal? = null,
        commission: BigDecimal? = null,
        notes: String? = null
    ): FinancialRecord {
        val existing = financialRecordRepository.findById(id)
            .orElseThrow { IllegalArgumentException("재무 기록을 찾을 수 없습니다: $id") }
        
        val newReceivedAmount = receivedAmount ?: existing.receivedAmount
        val newSalesAmount = salesAmount ?: existing.salesAmount
        val newOperatingCost = operatingCost ?: existing.operatingCost
        val newCommission = commission ?: existing.commission
        
        val newSubTotal = calculateSubTotal(newReceivedAmount, newSalesAmount, newOperatingCost)
        val newUnpaidAmount = calculateUnpaidAmount(newSubTotal, newCommission)
        
        val updated = existing.copy(
            receivedAmount = newReceivedAmount,
            salesAmount = newSalesAmount,
            operatingCost = newOperatingCost,
            subTotal = newSubTotal,
            commission = newCommission,
            unpaidAmount = newUnpaidAmount,
            notes = notes ?: existing.notes,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("재무 기록 업데이트 완료 - ID: ${updated.id}, SubTotal: ${updated.subTotal}")
        return financialRecordRepository.save(updated)
    }
    
    /**
     * Sub Total 계산: 수취송금 - 판매송금 - 운용비용
     */
    private fun calculateSubTotal(
        receivedAmount: BigDecimal,
        salesAmount: BigDecimal,
        operatingCost: BigDecimal
    ): BigDecimal {
        return receivedAmount
            .subtract(salesAmount)
            .subtract(operatingCost)
            .setScale(2, RoundingMode.HALF_UP)
    }
    
    /**
     * 미지급액 계산: SubTotal - 수수료
     */
    private fun calculateUnpaidAmount(subTotal: BigDecimal, commission: BigDecimal): BigDecimal {
        return subTotal
            .subtract(commission)
            .setScale(2, RoundingMode.HALF_UP)
    }
    
    /**
     * 수수료 자동 계산 (SubTotal의 일정 비율)
     */
    fun calculateCommission(subTotal: BigDecimal, commissionRate: BigDecimal = BigDecimal("0.10")): BigDecimal {
        return subTotal
            .multiply(commissionRate)
            .setScale(2, RoundingMode.HALF_UP)
    }
    
    /**
     * 파일별 재무 기록 조회
     */
    @Transactional(readOnly = true)
    fun getFinancialRecord(fileRecord: FileRecord): FinancialRecord? {
        return financialRecordRepository.findByFileRecord(fileRecord)
    }
    
    /**
     * 결제 상태 업데이트
     */
    fun updatePaymentStatus(id: Long, status: PaymentStatus): FinancialRecord {
        val existing = financialRecordRepository.findById(id)
            .orElseThrow { IllegalArgumentException("재무 기록을 찾을 수 없습니다: $id") }
        
        val updated = existing.copy(
            paymentStatus = status,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("결제 상태 업데이트 - ID: ${updated.id}, ${existing.paymentStatus} -> $status")
        return financialRecordRepository.save(updated)
    }
    
    /**
     * 결제 상태별 재무 기록 조회
     */
    @Transactional(readOnly = true)
    fun getFinancialRecordsByPaymentStatus(status: PaymentStatus): List<FinancialRecord> {
        return financialRecordRepository.findByPaymentStatus(status)
    }
    
    /**
     * 전체 미지급액 조회
     */
    @Transactional(readOnly = true)
    fun getTotalUnpaidAmount(): BigDecimal {
        return financialRecordRepository.getTotalUnpaidAmount().orElse(BigDecimal.ZERO)
    }
    
    /**
     * 결제 상태별 SubTotal 합계 조회
     */
    @Transactional(readOnly = true)
    fun getSubTotalByPaymentStatus(status: PaymentStatus): BigDecimal {
        return financialRecordRepository.sumSubTotalByPaymentStatus(status).orElse(BigDecimal.ZERO)
    }
    
    /**
     * 재무 요약 정보 조회
     */
    @Transactional(readOnly = true)
    fun getFinancialSummary(): FinancialSummary {
        val totalReceived = getAllFinancialRecords().sumOf { it.receivedAmount }
        val totalSales = getAllFinancialRecords().sumOf { it.salesAmount }
        val totalOperatingCost = getAllFinancialRecords().sumOf { it.operatingCost }
        val totalSubTotal = getAllFinancialRecords().sumOf { it.subTotal }
        val totalCommission = getAllFinancialRecords().sumOf { it.commission }
        val totalUnpaid = getTotalUnpaidAmount()
        
        val completedAmount = getSubTotalByPaymentStatus(PaymentStatus.COMPLETED)
        val pendingAmount = getSubTotalByPaymentStatus(PaymentStatus.PENDING)
        val overdueAmount = getSubTotalByPaymentStatus(PaymentStatus.OVERDUE)
        
        return FinancialSummary(
            totalReceived = totalReceived,
            totalSales = totalSales,
            totalOperatingCost = totalOperatingCost,
            totalSubTotal = totalSubTotal,
            totalCommission = totalCommission,
            totalUnpaid = totalUnpaid,
            completedAmount = completedAmount,
            pendingAmount = pendingAmount,
            overdueAmount = overdueAmount
        )
    }
    
    /**
     * 전체 재무 기록 조회
     */
    @Transactional(readOnly = true)
    fun getAllFinancialRecords(): List<FinancialRecord> {
        return financialRecordRepository.findAll()
    }
    
    /**
     * 재무 기록 삭제
     */
    fun deleteFinancialRecord(id: Long) {
        val financialRecord = financialRecordRepository.findById(id)
            .orElseThrow { IllegalArgumentException("재무 기록을 찾을 수 없습니다: $id") }
        
        logger.info("재무 기록 삭제 - ID: ${financialRecord.id}")
        financialRecordRepository.delete(financialRecord)
    }
    
    /**
     * 자동 수수료 계산 및 적용
     */
    fun applyAutoCommission(id: Long, commissionRate: BigDecimal = BigDecimal("0.10")): FinancialRecord {
        val existing = financialRecordRepository.findById(id)
            .orElseThrow { IllegalArgumentException("재무 기록을 찾을 수 없습니다: $id") }
        
        val calculatedCommission = calculateCommission(existing.subTotal, commissionRate)
        val newUnpaidAmount = calculateUnpaidAmount(existing.subTotal, calculatedCommission)
        
        val updated = existing.copy(
            commission = calculatedCommission,
            unpaidAmount = newUnpaidAmount,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("자동 수수료 적용 - ID: ${updated.id}, 수수료: ${updated.commission}")
        return financialRecordRepository.save(updated)
    }
}

/**
 * 재무 요약 정보 데이터 클래스
 */
data class FinancialSummary(
    val totalReceived: BigDecimal,
    val totalSales: BigDecimal,
    val totalOperatingCost: BigDecimal,
    val totalSubTotal: BigDecimal,
    val totalCommission: BigDecimal,
    val totalUnpaid: BigDecimal,
    val completedAmount: BigDecimal,
    val pendingAmount: BigDecimal,
    val overdueAmount: BigDecimal
) {
    val profitMargin: BigDecimal
        get() = if (totalReceived > BigDecimal.ZERO) {
            totalSubTotal.divide(totalReceived, 4, RoundingMode.HALF_UP).multiply(BigDecimal("100"))
        } else {
            BigDecimal.ZERO
        }
}