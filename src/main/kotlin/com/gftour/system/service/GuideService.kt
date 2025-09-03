package com.gftour.system.service

import com.gftour.system.dto.*
import com.gftour.system.entity.*
import com.gftour.system.repository.FileRecordRepository
import com.gftour.system.repository.GuideRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GuideService(
    private val guideRepository: GuideRepository,
    private val fileRecordRepository: FileRecordRepository
) {
    
    fun createOrUpdateGuide(request: GuideCreateRequest): GuideDto {
        val fileRecord = fileRecordRepository.findById(request.fileRecordId)
            .orElseThrow { IllegalArgumentException("파일 레코드를 찾을 수 없습니다") }
        
        val existingGuide = guideRepository.findByFileRecord(fileRecord)
        
        val guide = if (existingGuide != null) {
            // Update existing guide
            existingGuide.copy(
                guideName = request.guideName,
                guidePhone = request.guidePhone,
                schedule = request.schedule,
                safetyRules = request.safetyRules,
                precautions = request.precautions,
                groupActionPlan = request.groupActionPlan,
                meetingPoint = request.meetingPoint,
                emergencyContact = request.emergencyContact,
                status = request.status ?: existingGuide.status,
                updatedAt = LocalDateTime.now()
            )
        } else {
            // Create new guide
            Guide(
                fileRecord = fileRecord,
                guideName = request.guideName,
                guidePhone = request.guidePhone,
                schedule = request.schedule,
                safetyRules = request.safetyRules,
                precautions = request.precautions,
                groupActionPlan = request.groupActionPlan,
                meetingPoint = request.meetingPoint,
                emergencyContact = request.emergencyContact,
                status = request.status ?: GuideStatus.DRAFT
            )
        }
        
        val savedGuide = guideRepository.save(guide)
        return toDto(savedGuide)
    }
    
    fun getGuide(fileRecordId: Long): GuideDto? {
        val guide = guideRepository.findByFileRecordId(fileRecordId)
        return guide?.let { toDto(it) }
    }
    
    fun getAllGuides(): List<GuideDto> {
        val guides = guideRepository.findAll()
        return guides.map { toDto(it) }
    }
    
    fun getGuidesByGuideName(guideName: String): List<GuideDto> {
        val guides = guideRepository.findByGuideName(guideName)
        return guides.map { toDto(it) }
    }
    
    fun updateGuideStatus(id: Long, status: GuideStatus): GuideDto {
        val guide = guideRepository.findById(id)
            .orElseThrow { IllegalArgumentException("가이드 정보를 찾을 수 없습니다") }
        
        val updatedGuide = guide.copy(
            status = status,
            updatedAt = LocalDateTime.now()
        )
        
        val savedGuide = guideRepository.save(updatedGuide)
        return toDto(savedGuide)
    }
    
    fun createGroupActionPlan(fileRecordId: Long, plan: String): GuideDto {
        val guide = guideRepository.findByFileRecordId(fileRecordId)
            ?: throw IllegalArgumentException("가이드 정보를 찾을 수 없습니다")
        
        val updatedGuide = guide.copy(
            groupActionPlan = plan,
            status = GuideStatus.PREPARED,
            updatedAt = LocalDateTime.now()
        )
        
        val savedGuide = guideRepository.save(updatedGuide)
        return toDto(savedGuide)
    }
    
    private fun toDto(guide: Guide): GuideDto {
        return GuideDto(
            id = guide.id,
            fileRecordId = guide.fileRecord?.id ?: 0L,
            guideName = guide.guideName,
            guidePhone = guide.guidePhone,
            schedule = guide.schedule,
            safetyRules = guide.safetyRules,
            precautions = guide.precautions,
            groupActionPlan = guide.groupActionPlan,
            meetingPoint = guide.meetingPoint,
            emergencyContact = guide.emergencyContact,
            status = guide.status,
            createdAt = guide.createdAt,
            updatedAt = guide.updatedAt
        )
    }
}