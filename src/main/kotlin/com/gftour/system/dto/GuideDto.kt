package com.gftour.system.dto

import com.gftour.system.entity.GuideStatus
import java.time.LocalDateTime

// Guide DTOs
data class GuideCreateRequest(
    val fileRecordId: Long,
    val guideName: String,
    val guidePhone: String? = null,
    val schedule: String? = null,
    val safetyRules: String? = null,
    val precautions: String? = null,
    val groupActionPlan: String? = null,
    val meetingPoint: String? = null,
    val emergencyContact: String? = null,
    val status: GuideStatus? = null
)

data class GuideDto(
    val id: Long,
    val fileRecordId: Long,
    val guideName: String,
    val guidePhone: String?,
    val schedule: String?,
    val safetyRules: String?,
    val precautions: String?,
    val groupActionPlan: String?,
    val meetingPoint: String?,
    val emergencyContact: String?,
    val status: GuideStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)