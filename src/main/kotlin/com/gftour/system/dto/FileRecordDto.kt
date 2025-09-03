package com.gftour.system.dto

import com.gftour.system.entity.FileStatus
import java.time.LocalDateTime

// File Record DTOs
data class FileRecordCreateRequest(
    val destination: String,
    val manager: String,
    val agency: String? = null,
    val travelStartDate: LocalDateTime? = null,
    val travelEndDate: LocalDateTime? = null,
    val customerCount: Int = 0,
    val notes: String? = null
)

data class FileRecordUpdateRequest(
    val destination: String,
    val manager: String,
    val agency: String? = null,
    val travelStartDate: LocalDateTime? = null,
    val travelEndDate: LocalDateTime? = null,
    val customerCount: Int = 0,
    val status: FileStatus,
    val notes: String? = null
)

data class FileRecordDto(
    val id: Long,
    val refNo: String,
    val destination: String,
    val manager: String,
    val agency: String?,
    val travelStartDate: LocalDateTime?,
    val travelEndDate: LocalDateTime?,
    val customerCount: Int,
    val status: FileStatus,
    val notes: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class FileSearchRequest(
    val refNo: String? = null,
    val destination: String? = null,
    val manager: String? = null,
    val status: FileStatus? = null
)