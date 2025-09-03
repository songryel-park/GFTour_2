package com.gftour.system.dto

import java.time.LocalDateTime

// AGT DTOs
data class AGTCreateRequest(
    val name: String,
    val agencies: String? = null,
    val manager: String? = null,
    val address: String? = null,
    val region: String? = null,
    val tellNumber: String? = null,
    val email: String? = null,
    val post: String? = null,
    val notes: String? = null,
)

data class AGTUpdateRequest(
    val name: String,
    val agencies: String? = null,
    val manager: String? = null,
    val address: String? = null,
    val region: String? = null,
    val tellNumber: String? = null,
    val email: String? = null,
    val post: String? = null,
    val notes: String? = null,
)

data class AGTDto(
    val id: Long,
    val name: String,
    val agencies: String? = null,
    val manager: String? = null,
    val address: String? = null,
    val region: String? = null,
    val tellNumber: String? = null,
    val email: String? = null,
    val post: String? = null,
    val notes: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)