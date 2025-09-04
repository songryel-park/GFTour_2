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
            agencies = request.agencies,
            manager = request.manager,
            address = request.address,
            region = request.region,
            tellNumber = request.tellNumber,
            email = request.email,
            post = request.post,
            notes = request.notes,
        )
        
        val savedAGT = agtRepository.save(agt)
        return toDto(savedAGT)
    }
    
    fun updateAGT(id: Long, request: AGTUpdateRequest): AGTDto {
        val agt = agtRepository.findById(id)
            .orElseThrow { IllegalArgumentException("AGT를 찾을 수 없습니다") }
        
        val updatedAGT = agt.copy(
            name = request.name,
            agencies = request.agencies,
            manager = request.manager,
            address = request.address,
            region = request.region,
            tellNumber = request.tellNumber,
            email = request.email,
            post = request.post,
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
    
    fun searchAGTs(name: String?, region: String?): List<AGTDto> {
        val agts = agtRepository.searchAGTs(name, region)
        return agts.map { toDto(it) }
    }
    
    fun deleteAGT(id: Long) {
        if (!agtRepository.existsById(id)) {
            throw IllegalArgumentException("AGT를 찾을 수 없습니다")
        }
        agtRepository.deleteById(id)
    }
    
    fun getTotalAgtCount(): Long {
        return agtRepository.count()
    }
    
    private fun toDto(agt: AGT): AGTDto {
        return AGTDto(
            id = agt.id,
            name = agt.name,
            agencies = agt.agencies,
            manager = agt.manager,
            address = agt.address,
            region = agt.region,
            tellNumber = agt.tellNumber,
            email = agt.email,
            post = agt.post,
            notes = agt.notes,
            createdAt = agt.createdAt,
            updatedAt = agt.updatedAt
        )
    }
}