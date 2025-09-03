package com.gftour.service

import com.gftour.entity.*
import com.gftour.repository.FileRecordRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 파일 레코드 서비스
 * 신규등록 CRUD 기능, REF No 자동 생성 및 중복 검사
 */
@Service
@Transactional
class FileRecordService(
    private val fileRecordRepository: FileRecordRepository
) {
    private val logger = LoggerFactory.getLogger(FileRecordService::class.java)
    
    /**
     * REF No 자동 생성
     * 형식: GF-YYYYMMDD-XXX (XXX: 일련번호 3자리)
     */
    fun generateRefNo(): String {
        val today = LocalDate.now()
        val datePrefix = "GF-${today.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}"
        
        // 오늘 날짜로 시작하는 REF No 중 가장 큰 일련번호 찾기
        val existingRefNos = fileRecordRepository.findAll()
            .filter { it.refNo.startsWith(datePrefix) }
            .mapNotNull { 
                it.refNo.substringAfterLast("-").toIntOrNull()
            }
            .maxOrNull() ?: 0
        
        val nextSerial = String.format("%03d", existingRefNos + 1)
        return "$datePrefix-$nextSerial"
    }
    
    /**
     * REF No 중복 검사
     */
    fun isRefNoExists(refNo: String): Boolean {
        return fileRecordRepository.findByRefNo(refNo) != null
    }
    
    /**
     * 신규 파일 레코드 생성
     */
    fun createFileRecord(
        customer: Customer,
        tour: Tour,
        agt: AGT,
        paxCount: Int,
        departureDate: LocalDate,
        returnDate: LocalDate,
        fileCode: String,
        remarks: String? = null
    ): FileRecord {
        logger.info("신규 파일 레코드 생성 시작 - 고객: ${customer.name}")
        
        val refNo = generateRefNo()
        
        val fileRecord = FileRecord(
            refNo = refNo,
            fileCode = fileCode,
            customer = customer,
            tour = tour,
            agt = agt,
            paxCount = paxCount,
            departureDate = departureDate,
            returnDate = returnDate,
            remarks = remarks
        )
        
        val saved = fileRecordRepository.save(fileRecord)
        logger.info("파일 레코드 생성 완료 - REF No: ${saved.refNo}")
        
        return saved
    }
    
    /**
     * 파일 레코드 조회 by REF No
     */
    @Transactional(readOnly = true)
    fun findByRefNo(refNo: String): FileRecord? {
        return fileRecordRepository.findByRefNo(refNo)
    }
    
    /**
     * 파일 레코드 검색 및 필터링
     */
    @Transactional(readOnly = true)
    fun searchFileRecords(
        refNo: String? = null,
        customerName: String? = null,
        status: FileStatus? = null,
        pageable: Pageable
    ): Page<FileRecord> {
        return fileRecordRepository.searchFileRecords(refNo, customerName, status, pageable)
    }
    
    /**
     * 파일 레코드 상태 업데이트
     */
    fun updateStatus(id: Long, status: FileStatus): FileRecord {
        val fileRecord = fileRecordRepository.findById(id)
            .orElseThrow { IllegalArgumentException("파일 레코드를 찾을 수 없습니다: $id") }
        
        logger.info("파일 레코드 상태 변경 - REF No: ${fileRecord.refNo}, ${fileRecord.status} -> $status")
        
        val updated = fileRecord.copy(
            status = status,
            updatedAt = LocalDateTime.now()
        )
        
        return fileRecordRepository.save(updated)
    }
    
    /**
     * 파일 레코드 수정
     */
    fun updateFileRecord(
        id: Long,
        paxCount: Int? = null,
        remarks: String? = null
    ): FileRecord {
        val existing = fileRecordRepository.findById(id)
            .orElseThrow { IllegalArgumentException("파일 레코드를 찾을 수 없습니다: $id") }
        
        val updated = existing.copy(
            paxCount = paxCount ?: existing.paxCount,
            remarks = remarks ?: existing.remarks,
            updatedAt = LocalDateTime.now()
        )
        
        logger.info("파일 레코드 수정 완료 - REF No: ${updated.refNo}")
        return fileRecordRepository.save(updated)
    }
    
    /**
     * 파일 레코드 삭제
     */
    fun deleteFileRecord(id: Long) {
        val fileRecord = fileRecordRepository.findById(id)
            .orElseThrow { IllegalArgumentException("파일 레코드를 찾을 수 없습니다: $id") }
        
        logger.info("파일 레코드 삭제 - REF No: ${fileRecord.refNo}")
        fileRecordRepository.delete(fileRecord)
    }
    
    /**
     * AGT별 파일 레코드 조회
     */
    @Transactional(readOnly = true)
    fun findByAgt(agt: AGT): List<FileRecord> {
        return fileRecordRepository.findByAgt(agt)
    }
    
    /**
     * 기간별 파일 레코드 조회
     */
    @Transactional(readOnly = true)
    fun findByDateRange(startDate: LocalDate, endDate: LocalDate): List<FileRecord> {
        return fileRecordRepository.findByDepartureDateBetween(startDate, endDate)
    }
}