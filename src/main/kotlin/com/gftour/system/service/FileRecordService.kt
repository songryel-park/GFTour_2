package com.gftour.system.service

import com.gftour.system.dto.*
import com.gftour.system.entity.*
import com.gftour.system.repository.FileRecordRepository
import com.gftour.system.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class FileRecordService(
    private val fileRecordRepository: FileRecordRepository,
    private val userRepository: UserRepository
) {
    
    fun createFileRecord(request: FileRecordCreateRequest, createdByEmail: String): FileRecordDto {
        val refNo = generateRefNo()
        val createdBy = userRepository.findByEmail(createdByEmail).orElse(null)
        
        val fileRecord = FileRecord(
            refNo = refNo,
            destination = request.destination,
            manager = request.manager,
            agency = request.agency,
            travelStartDate = request.travelStartDate,
            travelEndDate = request.travelEndDate,
            customerCount = request.customerCount,
            notes = request.notes,
            createdBy = createdBy,
            status = FileStatus.DRAFT
        )
        
        val savedRecord = fileRecordRepository.save(fileRecord)
        return toDto(savedRecord)
    }
    
    fun updateFileRecord(id: Long, request: FileRecordUpdateRequest): FileRecordDto {
        val fileRecord = fileRecordRepository.findById(id)
            .orElseThrow { IllegalArgumentException("파일을 찾을 수 없습니다") }
        
        val updatedRecord = fileRecord.copy(
            destination = request.destination,
            manager = request.manager,
            agency = request.agency,
            travelStartDate = request.travelStartDate,
            travelEndDate = request.travelEndDate,
            customerCount = request.customerCount,
            status = request.status,
            notes = request.notes,
            updatedAt = LocalDateTime.now()
        )
        
        val savedRecord = fileRecordRepository.save(updatedRecord)
        return toDto(savedRecord)
    }
    
    fun getFileRecord(id: Long): FileRecordDto {
        val fileRecord = fileRecordRepository.findById(id)
            .orElseThrow { IllegalArgumentException("파일을 찾을 수 없습니다") }
        return toDto(fileRecord)
    }
    
    fun getFileRecordByRefNo(refNo: String): FileRecordDto? {
        val fileRecord = fileRecordRepository.findByRefNo(refNo)
        return fileRecord?.let { toDto(it) }
    }
    
    fun deleteFileRecord(id: Long) {
        if (!fileRecordRepository.existsById(id)) {
            throw IllegalArgumentException("파일을 찾을 수 없습니다")
        }
        fileRecordRepository.deleteById(id)
    }
    
    fun searchFiles(searchRequest: FileSearchRequest, page: Int, size: Int): Page<FileRecordDto> {
        val pageable = PageRequest.of(page, size)
        val files = fileRecordRepository.searchFiles(
            refNo = searchRequest.refNo,
            destination = searchRequest.destination,
            manager = searchRequest.manager,
            status = searchRequest.status,
            pageable = pageable
        )
        
        return files.map { toDto(it) }
    }
    
    fun saveFile(id: Long): FileRecordDto {
        val fileRecord = fileRecordRepository.findById(id)
            .orElseThrow { IllegalArgumentException("파일을 찾을 수 없습니다") }
        
        val updatedRecord = fileRecord.copy(
            status = FileStatus.IN_PROGRESS,
            updatedAt = LocalDateTime.now()
        )
        
        val savedRecord = fileRecordRepository.save(updatedRecord)
        return toDto(savedRecord)
    }
    
    fun cancelFile(id: Long): FileRecordDto {
        val fileRecord = fileRecordRepository.findById(id)
            .orElseThrow { IllegalArgumentException("파일을 찾을 수 없습니다") }
        
        val updatedRecord = fileRecord.copy(
            status = FileStatus.CANCELLED,
            updatedAt = LocalDateTime.now()
        )
        
        val savedRecord = fileRecordRepository.save(updatedRecord)
        return toDto(savedRecord)
    }
    
    private fun generateRefNo(): String {
        val currentDate = LocalDateTime.now()
        val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
        val dateString = currentDate.format(dateFormat)
        
        // Format: GF + YYYYMMDD + 3-digit sequence
        var sequence = 1
        var refNo: String
        
        do {
            refNo = "GF$dateString${sequence.toString().padStart(3, '0')}"
            sequence++
        } while (fileRecordRepository.existsByRefNo(refNo))
        
        return refNo
    }
    
    private fun toDto(fileRecord: FileRecord): FileRecordDto {
        return FileRecordDto(
            id = fileRecord.id,
            refNo = fileRecord.refNo,
            destination = fileRecord.destination,
            manager = fileRecord.manager,
            agency = fileRecord.agency,
            travelStartDate = fileRecord.travelStartDate,
            travelEndDate = fileRecord.travelEndDate,
            customerCount = fileRecord.customerCount,
            status = fileRecord.status,
            notes = fileRecord.notes,
            createdAt = fileRecord.createdAt,
            updatedAt = fileRecord.updatedAt
        )
    }
}