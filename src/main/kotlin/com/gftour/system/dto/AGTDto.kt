package com.gftour.system.dto

import java.time.LocalDateTime

// AGT DTOs
data class AGTCreateRequest(
    val name: String,
    val contactPerson: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val region: String? = null,
    val country: String? = null,
    val businessLicense: String? = null,
    val notes: String? = null
)

data class AGTUpdateRequest(
    val name: String,
    val contactPerson: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val region: String? = null,
    val country: String? = null,
    val businessLicense: String? = null,
    val notes: String? = null,
    val active: Boolean = true
)

data class AGTDto(
    val id: Long,
    val name: String,
    val contactPerson: String?,
    val phoneNumber: String?,
    val email: String?,
    val address: String?,
    val region: String?,
    val country: String?,
    val businessLicense: String?,
    val notes: String?,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)