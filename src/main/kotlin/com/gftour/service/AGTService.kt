package com.gftour.service

import com.gftour.entity.*
import com.gftour.repository.AGTRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * AGT(에이전트) 서비스
 * 에이전트 정보 관리, 검색 및 필터링
 */
@Service
@Transactional
class AGTService(
    private val agtRepository: AGTRepository,
    private val fileRecordService: FileRecordService
) {
    private val logger = LoggerFactory.getLogger(AGTService::class.java)
    
    /**
     * 새로운 AGT 등록
     */
    fun createAGT(
        agtCode: String,
        name: String,
        address: String? = null,
        phone: String? = null,
        fax: String? = null,
        contactPerson: String? = null,
        email: String? = null,
        notes: String? = null
    ): AGT {
        logger.info("AGT 등록 시작 - 코드: $agtCode, 명: $name")
        
        // AGT 코드 중복 검사
        if (agtRepository.findByAgtCode(agtCode) != null) {
            throw IllegalArgumentException("이미 존재하는 AGT 코드입니다: $agtCode")
        }
        
        val agt = AGT(
            agtCode = agtCode,
            name = name,
            address = address,
            phone = phone,
            fax = fax,
            contactPerson = contactPerson,
            email = email,
            notes = notes
        )
        
        val saved = agtRepository.save(agt)
        logger.info("AGT 등록 완료 - ID: ${saved.id}, 코드: ${saved.agtCode}")
        
        return saved
    }
    
    /**
     * AGT 정보 수정
     */
    fun updateAGT(
        id: Long,
        name: String? = null,
        address: String? = null,
        phone: String? = null,
        fax: String? = null,
        contactPerson: String? = null,
        email: String? = null,
        notes: String? = null
    ): AGT {
        val existing = agtRepository.findById(id)
            .orElseThrow { IllegalArgumentException("AGT를 찾을 수 없습니다: $id") }
        
        val updated = existing.copy(
            name = name ?: existing.name,
            address = address ?: existing.address,
            phone = phone ?: existing.phone,
            fax = fax ?: existing.fax,
            contactPerson = contactPerson ?: existing.contactPerson,
            email = email ?: existing.email,
            notes = notes ?: existing.notes,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("AGT 정보 수정 완료 - 코드: ${updated.agtCode}")
        return agtRepository.save(updated)
    }
    
    /**
     * AGT 상태 변경
     */
    fun updateAGTStatus(id: Long, status: AGTStatus): AGT {
        val existing = agtRepository.findById(id)
            .orElseThrow { IllegalArgumentException("AGT를 찾을 수 없습니다: $id") }
        
        val updated = existing.copy(
            status = status,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("AGT 상태 변경 - 코드: ${updated.agtCode}, ${existing.status} -> $status")
        return agtRepository.save(updated)
    }
    
    /**
     * AGT 조회 by 코드
     */
    @Transactional(readOnly = true)
    fun findByAgtCode(agtCode: String): AGT? {
        return agtRepository.findByAgtCode(agtCode)
    }
    
    /**
     * AGT 조회 by ID
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): AGT? {
        return agtRepository.findById(id).orElse(null)
    }
    
    /**
     * AGT 이름으로 검색
     */
    @Transactional(readOnly = true)
    fun searchByName(name: String): List<AGT> {
        return agtRepository.findByNameContainingIgnoreCase(name)
    }
    
    /**
     * 상태별 AGT 목록 조회
     */
    @Transactional(readOnly = true)
    fun findByStatus(status: AGTStatus): List<AGT> {
        return agtRepository.findByStatus(status)
    }
    
    /**
     * 활성 AGT 목록 조회
     */
    @Transactional(readOnly = true)
    fun getActiveAGTs(): List<AGT> {
        return agtRepository.findByStatus(AGTStatus.ACTIVE)
    }
    
    /**
     * 전체 AGT 목록 조회
     */
    @Transactional(readOnly = true)
    fun getAllAGTs(): List<AGT> {
        return agtRepository.findAll()
    }
    
    /**
     * 담당자별 AGT 검색
     */
    @Transactional(readOnly = true)
    fun findByContactPerson(contactPerson: String): List<AGT> {
        return agtRepository.findByContactPerson(contactPerson)
    }
    
    /**
     * AGT별 배정된 파일 목록 조회
     */
    @Transactional(readOnly = true)
    fun getAssignedFiles(agt: AGT): List<FileRecord> {
        return fileRecordService.findByAgt(agt)
    }
    
    /**
     * AGT별 업무 통계 조회
     */
    @Transactional(readOnly = true)
    fun getAGTStatistics(agt: AGT): AGTStatistics {
        val assignedFiles = getAssignedFiles(agt)
        
        val totalFiles = assignedFiles.size
        val completedFiles = assignedFiles.count { it.status == FileStatus.COMPLETED }
        val inProgressFiles = assignedFiles.count { it.status == FileStatus.IN_PROGRESS }
        val newFiles = assignedFiles.count { it.status == FileStatus.NEW }
        
        return AGTStatistics(
            agt = agt,
            totalFiles = totalFiles,
            completedFiles = completedFiles,
            inProgressFiles = inProgressFiles,
            newFiles = newFiles
        )
    }
    
    /**
     * AGT 삭제
     */
    fun deleteAGT(id: Long) {
        val agt = agtRepository.findById(id)
            .orElseThrow { IllegalArgumentException("AGT를 찾을 수 없습니다: $id") }
        
        // 배정된 파일이 있는지 확인
        val assignedFiles = getAssignedFiles(agt)
        if (assignedFiles.isNotEmpty()) {
            throw IllegalStateException("배정된 파일이 있는 AGT는 삭제할 수 없습니다. 먼저 파일을 다른 AGT에게 재배정하세요.")
        }
        
        logger.info("AGT 삭제 - 코드: ${agt.agtCode}")
        agtRepository.delete(agt)
    }
    
    /**
     * AGT 코드 중복 검사
     */
    @Transactional(readOnly = true)
    fun isAgtCodeExists(agtCode: String): Boolean {
        return agtRepository.findByAgtCode(agtCode) != null
    }
}

/**
 * AGT 업무 통계 데이터 클래스
 */
data class AGTStatistics(
    val agt: AGT,
    val totalFiles: Int,
    val completedFiles: Int,
    val inProgressFiles: Int,
    val newFiles: Int
) {
    val completionRate: Double
        get() = if (totalFiles > 0) (completedFiles.toDouble() / totalFiles) * 100 else 0.0
}