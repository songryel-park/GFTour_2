package com.gftour.system.service

import com.gftour.system.dto.AGTCreateRequest
import com.gftour.system.dto.AGTDto
import com.gftour.system.dto.AGTUpdateRequest
import com.gftour.system.entity.AGT
import com.gftour.system.repository.AGTRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AGTService(
    private val agtRepository: AGTRepository
) {
    
    fun createAGT(request: AGTCreateRequest): AGTDto {
        val agt = AGT(
            name = request.name,
            contactPerson = request.contactPerson,
            phoneNumber = request.phoneNumber,
            email = request.email,
            address = request.address,
            region = request.region,
            country = request.country,
            notes = request.notes
        )
        
        val savedAGT = agtRepository.save(agt)
        return toDto(savedAGT)
    }
    
    fun updateAGT(id: Long, request: AGTUpdateRequest): AGTDto {
        val agt = agtRepository.findById(id)
            .orElseThrow { IllegalArgumentException("AGT를 찾을 수 없습니다") }
        
        val updatedAGT = agt.copy(
            name = request.name,
            contactPerson = request.contactPerson,
            phoneNumber = request.phoneNumber,
            email = request.email,
            address = request.address,
            region = request.region,
            country = request.country,
            notes = request.notes,
            updatedAt = LocalDateTime.now()
        )
        
        val savedAGT = agtRepository.save(updatedAGT)
        return toDto(savedAGT)
    }
    
    fun getAGT(id: Long): AGTDto {
        val agt = agtRepository.findById(id)
            .orElseThrow { IllegalArgumentException("AGT를 찾을 수 없습니다") }
        return toDto(agt)
    }
    
    fun getAllAGTs(): List<AGTDto> {
        val agts = agtRepository.findAll()
        return agts.map { toDto(it) }
    }
    
    fun searchAGTs(name: String?, region: String?, country: String?): List<AGTDto> {
        val agts = agtRepository.searchAGTs(name, region, country)
        return agts.map { toDto(it) }
    }
    
    fun deleteAGT(id: Long) {
        if (!agtRepository.existsById(id)) {
            throw IllegalArgumentException("AGT를 찾을 수 없습니다")
        }
        agtRepository.deleteById(id)
    }
    
    private fun toDto(agt: AGT): AGTDto {
        return AGTDto(
            id = agt.id,
            name = agt.name,
            contactPerson = agt.contactPerson,
            phoneNumber = agt.phoneNumber,
            email = agt.email,
            address = agt.address,
            region = agt.region,
            country = agt.country,
            notes = agt.notes,
            createdAt = agt.createdAt,
            updatedAt = agt.updatedAt
        )
    }
}