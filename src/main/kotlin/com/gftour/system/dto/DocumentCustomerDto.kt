package com.gftour.system.dto

import com.gftour.system.entity.DocumentType
import com.gftour.system.entity.DocumentStatus
import java.math.BigDecimal
import java.time.LocalDateTime

// Document DTOs
data class DocumentCreateRequest(
    val fileRecordId: Long,
    val type: DocumentType,
    val content: String? = null,
    val amount: BigDecimal? = null
)

data class DocumentUpdateRequest(
    val content: String? = null,
    val amount: BigDecimal? = null,
    val status: DocumentStatus? = null
)

data class DocumentDto(
    val id: Long,
    val fileRecordId: Long,
    val type: DocumentType,
    val status: DocumentStatus,
    val content: String?,
    val amount: BigDecimal?,
    val approvedBy: String?,
    val approvedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

// Customer DTOs
data class CustomerCreateRequest(
    val name: String,
    val age: Int,
    val passportNumber: String,
    val fileRecordId: Long? = null
)

data class CustomerUpdateRequest(
    val name: String,
    val age: Int,
    val passportNumber: String,
)

data class CustomerDto(
    val id: Long,
    val name: String,
    val age: Int,
    val passportNumber: String,
    val fileRecordId: Long?
)