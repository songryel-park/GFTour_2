package com.gftour.system.service

import com.gftour.system.dto.*
import com.gftour.system.entity.*
import com.gftour.system.repository.FileRecordRepository
import com.gftour.system.repository.FinancialRecordRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class FinancialService(
    private val financialRecordRepository: FinancialRecordRepository,
    private val fileRecordRepository: FileRecordRepository
) {
    
    fun createOrUpdateFinancialRecord(request: FinancialRecordRequest): FinancialRecordDto {
        val fileRecord = fileRecordRepository.findById(request.fileRecordId)
            .orElseThrow { IllegalArgumentException("파일 레코드를 찾을 수 없습니다") }
        
        val existingRecord = financialRecordRepository.findByFileRecord(fileRecord)
        
        val financialRecord = if (existingRecord != null) {
            // Update existing record
            existingRecord.copy(
                receiptAmount = request.receiptAmount,
                salesAmount = request.salesAmount,
                operatingCost = request.operatingCost,
                subTotal = calculateSubTotal(request.salesAmount, request.receiptAmount, request.operatingCost),
                commissionRate = request.commissionRate,
                commissionAmount = calculateCommissionAmount(request.salesAmount, request.commissionRate),
                status = request.status ?: existingRecord.status,
                updatedAt = LocalDateTime.now()
            )
        } else {
            // Create new record
            FinancialRecord(
                fileRecord = fileRecord,
                receiptAmount = request.receiptAmount,
                salesAmount = request.salesAmount,
                operatingCost = request.operatingCost,
                subTotal = calculateSubTotal(request.salesAmount, request.receiptAmount, request.operatingCost),
                commissionRate = request.commissionRate,
                commissionAmount = calculateCommissionAmount(request.salesAmount, request.commissionRate),
                status = request.status ?: FinancialStatus.DRAFT
            )
        }
        
        val savedRecord = financialRecordRepository.save(financialRecord)
        return toDto(savedRecord)
    }
    
    fun getFinancialRecord(fileRecordId: Long): FinancialRecordDto? {
        val financialRecord = financialRecordRepository.findByFileRecordId(fileRecordId)
        return financialRecord?.let { toDto(it) }
    }
    
    fun getFinancialReports(): List<FinancialRecordDto> {
        val reports = financialRecordRepository.findAll()
        return reports.map { toDto(it) }
    }
    
    fun getFinancialReportsByPeriod(startDate: LocalDateTime, endDate: LocalDateTime): List<FinancialRecordDto> {
        val reports = financialRecordRepository.findByDateRange(startDate, endDate)
        return reports.map { toDto(it) }
    }
    
    fun calculateCommissionForFile(fileRecordId: Long): FinancialCommissionDto {
        val financialRecord = financialRecordRepository.findByFileRecordId(fileRecordId)
            ?: throw IllegalArgumentException("정산 정보를 찾을 수 없습니다")
        
        val commissionAmount = financialRecord.commissionAmount ?: BigDecimal.ZERO
        val netAmount = financialRecord.subTotal.subtract(commissionAmount)
        
        return FinancialCommissionDto(
            fileRecordId = fileRecordId,
            salesAmount = financialRecord.salesAmount,
            receiptAmount = financialRecord.receiptAmount,
            operatingCost = financialRecord.operatingCost,
            subTotal = financialRecord.subTotal,
            commissionRate = financialRecord.commissionRate,
            commissionAmount = commissionAmount,
            netAmount = netAmount
        )
    }
    
    fun generateReceipt(fileRecordId: Long): FinancialReceiptDto {
        val financialRecord = financialRecordRepository.findByFileRecordId(fileRecordId)
            ?: throw IllegalArgumentException("정산 정보를 찾을 수 없습니다")
        
        val fileRecord = financialRecord.fileRecord
            ?: throw IllegalArgumentException("파일 레코드를 찾을 수 없습니다")
        
        return FinancialReceiptDto(
            refNo = fileRecord.refNo,
            destination = fileRecord.destination,
            manager = fileRecord.manager,
            salesAmount = financialRecord.salesAmount,
            receiptAmount = financialRecord.receiptAmount,
            operatingCost = financialRecord.operatingCost,
            subTotal = financialRecord.subTotal,
            commissionAmount = financialRecord.commissionAmount ?: BigDecimal.ZERO,
            issueDate = LocalDateTime.now()
        )
    }
    
    fun getMonthlySettlement(): String {
        val currentMonth = LocalDateTime.now()
        val startOfMonth = currentMonth.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        val endOfMonth = currentMonth.withDayOfMonth(currentMonth.month.length(currentMonth.year % 4 == 0)).withHour(23).withMinute(59).withSecond(59)
        
        val monthlyRecords = financialRecordRepository.findByDateRange(startOfMonth, endOfMonth)
        val totalSettlement = monthlyRecords.sumOf { it.subTotal }
        
        return "₩${String.format("%,d", totalSettlement.toLong())}"
    }
    
    private fun calculateSubTotal(salesAmount: BigDecimal, receiptAmount: BigDecimal, operatingCost: BigDecimal): BigDecimal {
        return salesAmount.subtract(receiptAmount).subtract(operatingCost)
    }
    
    private fun calculateCommissionAmount(salesAmount: BigDecimal, commissionRate: BigDecimal?): BigDecimal? {
        return commissionRate?.let { rate ->
            salesAmount.multiply(rate).divide(BigDecimal(100))
        }
    }
    
    private fun toDto(financialRecord: FinancialRecord): FinancialRecordDto {
        return FinancialRecordDto(
            id = financialRecord.id,
            fileRecordId = financialRecord.fileRecord?.id ?: 0L,
            receiptAmount = financialRecord.receiptAmount,
            salesAmount = financialRecord.salesAmount,
            operatingCost = financialRecord.operatingCost,
            subTotal = financialRecord.subTotal,
            commissionRate = financialRecord.commissionRate,
            commissionAmount = financialRecord.commissionAmount,
            status = financialRecord.status,
            createdAt = financialRecord.createdAt,
            updatedAt = financialRecord.updatedAt
        )
    }
}