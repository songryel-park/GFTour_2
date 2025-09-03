package com.gftour.service

import com.gftour.entity.*
import com.gftour.repository.GuideInstructionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 가이드 지침서 서비스
 * 단체행동계획서 및 가이드 정보 관리
 */
@Service
@Transactional
class GuideService(
    private val guideInstructionRepository: GuideInstructionRepository
) {
    private val logger = LoggerFactory.getLogger(GuideService::class.java)
    
    /**
     * 가이드 지침서 생성
     */
    fun createGuideInstruction(
        fileRecord: FileRecord,
        guideName: String,
        guidePhone: String? = null,
        travelSchedule: String? = null,
        safetyRules: String? = null,
        precautions: String? = null,
        emergencyContact: String? = null,
        specialInstructions: String? = null,
        createdBy: User
    ): GuideInstruction {
        logger.info("가이드 지침서 생성 시작 - REF No: ${fileRecord.refNo}, 가이드: $guideName")
        
        // 이미 지침서가 존재하는지 확인
        val existing = guideInstructionRepository.findByFileRecord(fileRecord)
        if (existing != null) {
            throw IllegalStateException("해당 파일에 이미 가이드 지침서가 존재합니다")
        }
        
        val guideInstruction = GuideInstruction(
            fileRecord = fileRecord,
            guideName = guideName,
            guidePhone = guidePhone,
            travelSchedule = travelSchedule,
            safetyRules = safetyRules,
            precautions = precautions,
            emergencyContact = emergencyContact,
            specialInstructions = specialInstructions,
            createdBy = createdBy
        )
        
        val saved = guideInstructionRepository.save(guideInstruction)
        logger.info("가이드 지침서 생성 완료 - ID: ${saved.id}, 가이드: ${saved.guideName}")
        
        return saved
    }
    
    /**
     * 템플릿 기반 가이드 지침서 생성
     */
    fun createGuideInstructionFromTemplate(
        fileRecord: FileRecord,
        guideName: String,
        guidePhone: String? = null,
        createdBy: User
    ): GuideInstruction {
        val (schedule, safetyRules, precautions, emergencyContact) = 
            getGuideInstructionTemplate(fileRecord)
        
        return createGuideInstruction(
            fileRecord = fileRecord,
            guideName = guideName,
            guidePhone = guidePhone,
            travelSchedule = schedule,
            safetyRules = safetyRules,
            precautions = precautions,
            emergencyContact = emergencyContact,
            createdBy = createdBy
        )
    }
    
    /**
     * 가이드 지침서 템플릿 생성
     */
    private fun getGuideInstructionTemplate(fileRecord: FileRecord): GuideTemplate {
        val customer = fileRecord.customer
        val tour = fileRecord.tour
        
        val travelSchedule = """
            ■ 여행 일정표
            
            여행 기간: ${fileRecord.departureDate} ~ ${fileRecord.returnDate}
            고객명: ${customer.name}
            인원: ${fileRecord.paxCount}명
            
            [1일차]
            - 출발 공항 집결 및 출국 수속
            - 목적지 도착 후 가이드 미팅
            - 호텔 체크인 및 일정 안내
            
            [일정별 주요 포인트]
            - 안전사고 예방을 위한 주의사항 안내
            - 현지 문화 및 관습 설명
            - 쇼핑 및 자유시간 안내
            
            ※ 상세 일정은 현지 상황에 따라 변경될 수 있습니다.
        """.trimIndent()
        
        val safetyRules = """
            ■ 안전 수칙
            
            1. 단체 행동 원칙
            - 가이드의 지시에 따라 행동
            - 개별 행동 시 반드시 가이드에게 사전 통보
            - 집결 시간 및 장소 엄수
            
            2. 응급상황 대응
            - 응급상황 발생 시 즉시 가이드에게 연락
            - 의료진 연락 및 병원 이송 절차
            - 여행자 보험 처리 안내
            
            3. 개인 안전 관리
            - 귀중품 보관 주의
            - 현지 법규 및 관습 준수
            - 음식 및 음료 섭취 시 주의사항
        """.trimIndent()
        
        val precautions = """
            ■ 주의사항
            
            1. 고객별 특이사항
            - 알레르기 여부 확인
            - 복용 중인 약물 파악
            - 신체적 제약사항 숙지
            
            2. 현지 주의사항
            - 기후 및 날씨 특성
            - 치안 상황 및 주의지역
            - 통화 및 환전 안내
            
            3. 쇼핑 및 옵션투어
            - 강요 금지 원칙
            - 가격 협상 및 환불 정책
            - 가이드 수수료 투명성
        """.trimIndent()
        
        val emergencyContact = """
            ■ 비상 연락처
            
            1. 현지 연락처
            - 가이드 휴대폰: ${fileRecord.agt.phone ?: "미정"}
            - 현지 사무소: 
            - 현지 병원:
            
            2. 한국 연락처
            - 본사 24시간 핫라인: 02-000-0000
            - 담당자: ${fileRecord.agt.contactPerson ?: "미정"}
            - 응급상황 대응팀: 
            
            3. 공식 기관
            - 현지 한국 영사관:
            - 현지 경찰서:
            - 국제 SOS:
        """.trimIndent()
        
        return GuideTemplate(
            travelSchedule = travelSchedule,
            safetyRules = safetyRules,
            precautions = precautions,
            emergencyContact = emergencyContact
        )
    }
    
    /**
     * 가이드 지침서 수정
     */
    fun updateGuideInstruction(
        id: Long,
        guideName: String? = null,
        guidePhone: String? = null,
        travelSchedule: String? = null,
        safetyRules: String? = null,
        precautions: String? = null,
        emergencyContact: String? = null,
        specialInstructions: String? = null
    ): GuideInstruction {
        val existing = guideInstructionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("가이드 지침서를 찾을 수 없습니다: $id") }
        
        if (existing.status == GuideInstructionStatus.DISTRIBUTED) {
            throw IllegalStateException("이미 배포된 지침서는 수정할 수 없습니다")
        }
        
        val updated = existing.copy(
            guideName = guideName ?: existing.guideName,
            guidePhone = guidePhone ?: existing.guidePhone,
            travelSchedule = travelSchedule ?: existing.travelSchedule,
            safetyRules = safetyRules ?: existing.safetyRules,
            precautions = precautions ?: existing.precautions,
            emergencyContact = emergencyContact ?: existing.emergencyContact,
            specialInstructions = specialInstructions ?: existing.specialInstructions,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("가이드 지침서 수정 완료 - ID: ${updated.id}")
        return guideInstructionRepository.save(updated)
    }
    
    /**
     * 가이드 지침서 상태 변경
     */
    fun updateGuideInstructionStatus(id: Long, status: GuideInstructionStatus): GuideInstruction {
        val existing = guideInstructionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("가이드 지침서를 찾을 수 없습니다: $id") }
        
        val updated = existing.copy(
            status = status,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("가이드 지침서 상태 변경 - ID: ${updated.id}, ${existing.status} -> $status")
        return guideInstructionRepository.save(updated)
    }
    
    /**
     * 파일별 가이드 지침서 조회
     */
    @Transactional(readOnly = true)
    fun getGuideInstructionByFileRecord(fileRecord: FileRecord): GuideInstruction? {
        return guideInstructionRepository.findByFileRecord(fileRecord)
    }
    
    /**
     * 가이드명으로 지침서 목록 조회
     */
    @Transactional(readOnly = true)
    fun getGuideInstructionsByGuide(guideName: String): List<GuideInstruction> {
        return guideInstructionRepository.findByGuideName(guideName)
    }
    
    /**
     * 상태별 가이드 지침서 조회
     */
    @Transactional(readOnly = true)
    fun getGuideInstructionsByStatus(status: GuideInstructionStatus): List<GuideInstruction> {
        return guideInstructionRepository.findByStatus(status)
    }
    
    /**
     * 작성자별 가이드 지침서 조회
     */
    @Transactional(readOnly = true)
    fun getGuideInstructionsByCreator(user: User): List<GuideInstruction> {
        return guideInstructionRepository.findByCreatedBy(user)
    }
    
    /**
     * 가이드 지침서 확정
     */
    fun finalizeGuideInstruction(id: Long): GuideInstruction {
        return updateGuideInstructionStatus(id, GuideInstructionStatus.FINALIZED)
    }
    
    /**
     * 가이드 지침서 배포
     */
    fun distributeGuideInstruction(id: Long): GuideInstruction {
        val existing = guideInstructionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("가이드 지침서를 찾을 수 없습니다: $id") }
        
        if (existing.status != GuideInstructionStatus.FINALIZED) {
            throw IllegalStateException("확정된 지침서만 배포할 수 있습니다")
        }
        
        return updateGuideInstructionStatus(id, GuideInstructionStatus.DISTRIBUTED)
    }
    
    /**
     * 가이드 지침서 삭제
     */
    fun deleteGuideInstruction(id: Long) {
        val guideInstruction = guideInstructionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("가이드 지침서를 찾을 수 없습니다: $id") }
        
        if (guideInstruction.status == GuideInstructionStatus.DISTRIBUTED) {
            throw IllegalStateException("배포된 지침서는 삭제할 수 없습니다")
        }
        
        logger.info("가이드 지침서 삭제 - ID: ${guideInstruction.id}")
        guideInstructionRepository.delete(guideInstruction)
    }
    
    /**
     * 전체 가이드 지침서 목록 조회
     */
    @Transactional(readOnly = true)
    fun getAllGuideInstructions(): List<GuideInstruction> {
        return guideInstructionRepository.findAll()
    }
}

/**
 * 가이드 지침서 템플릿 데이터 클래스
 */
data class GuideTemplate(
    val travelSchedule: String,
    val safetyRules: String,
    val precautions: String,
    val emergencyContact: String
)