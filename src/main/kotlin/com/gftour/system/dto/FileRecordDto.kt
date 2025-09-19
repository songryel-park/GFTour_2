package com.gftour.system.dto

import com.gftour.system.entity.FileStatus
import java.time.LocalDateTime

// File Record DTOs
data class FileRecordCreateRequest(
    val groupNumber: String,
    val group: String? = null,
    val tourTitle: String,
    val manager: String,
    val agency: String,
    val travelStartDate: LocalDateTime? = null,
    val travelEndDate: LocalDateTime? = null,
    val customerCount: Int = 0,
    val notes: String? = null
)

data class FileRecordUpdateRequest(
    val groupNumber: String,
    val group: String? = null,
    val tourTitle: String,
    val manager: String,
    val agency: String,
    val travelStartDate: LocalDateTime? = null,
    val travelEndDate: LocalDateTime? = null,
    val customerCount: Int = 0,
    val status: FileStatus,
    val notes: String? = null
)

data class FileRecordDto(
    val id: Long,
    val refNo: String,
    val groupNumber: String,
    val group: String? = null,
    val tourTitle: String,
    val manager: String,
    val agency: String,
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
    val groupNumber: String? = null,
    val manager: String? = null,
    val status: FileStatus? = null
)