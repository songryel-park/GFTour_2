package com.gftour.system.dto

import com.gftour.system.entity.FinancialStatus
import java.math.BigDecimal
import java.time.LocalDateTime

// Financial DTOs
data class FinancialRecordRequest(
    val fileRecordId: Long,
    val receiptAmount: BigDecimal,
    val salesAmount: BigDecimal,
    val operatingCost: BigDecimal,
    val commissionRate: BigDecimal? = null,
    val status: FinancialStatus? = null
)

data class FinancialRecordDto(
    val id: Long,
    val fileRecordId: Long,
    val receiptAmount: BigDecimal,
    val salesAmount: BigDecimal,
    val operatingCost: BigDecimal,
    val subTotal: BigDecimal,
    val commissionRate: BigDecimal?,
    val commissionAmount: BigDecimal?,
    val status: FinancialStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class FinancialCommissionDto(
    val fileRecordId: Long,
    val salesAmount: BigDecimal,
    val receiptAmount: BigDecimal,
    val operatingCost: BigDecimal,
    val subTotal: BigDecimal,
    val commissionRate: BigDecimal?,
    val commissionAmount: BigDecimal,
    val netAmount: BigDecimal
)

data class FinancialReceiptDto(
    val refNo: String,
    val destination: String,
    val manager: String,
    val salesAmount: BigDecimal,
    val receiptAmount: BigDecimal,
    val operatingCost: BigDecimal,
    val subTotal: BigDecimal,
    val commissionAmount: BigDecimal,
    val issueDate: LocalDateTime
)