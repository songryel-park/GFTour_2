package com.gftour.service

import com.gftour.entity.*
import com.gftour.repository.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.time.LocalDate
import java.util.*

/**
 * FileRecordService 테스트
 */
@SpringJUnitConfig
class FileRecordServiceTest {
    
    private val fileRecordRepository = mockk<FileRecordRepository>()
    private val fileRecordService = FileRecordService(fileRecordRepository)
    
    @Test
    fun `REF No 자동 생성 테스트`() {
        // Given
        every { fileRecordRepository.findAll() } returns emptyList()
        
        // When
        val refNo = fileRecordService.generateRefNo()
        
        // Then
        val today = LocalDate.now()
        val expectedPrefix = "GF-${today.year}${String.format("%02d", today.monthValue)}${String.format("%02d", today.dayOfMonth)}"
        assert(refNo.startsWith(expectedPrefix))
        assert(refNo.endsWith("-001"))
    }
    
    @Test
    fun `REF No 중복 검사 테스트`() {
        // Given
        val refNo = "GF-20240101-001"
        val existingRecord = mockk<FileRecord>()
        every { fileRecordRepository.findByRefNo(refNo) } returns existingRecord
        
        // When
        val exists = fileRecordService.isRefNoExists(refNo)
        
        // Then
        assert(exists)
        verify { fileRecordRepository.findByRefNo(refNo) }
    }
    
    @Test
    fun `파일 레코드 생성 테스트`() {
        // Given
        val customer = mockk<Customer>()
        val tour = mockk<Tour>()
        val agt = mockk<AGT>()
        val fileRecord = mockk<FileRecord>()
        
        every { customer.name } returns "테스트 고객"
        every { fileRecordRepository.findAll() } returns emptyList()
        every { fileRecordRepository.save(any()) } returns fileRecord
        every { fileRecord.refNo } returns "GF-20240101-001"
        
        // When
        val result = fileRecordService.createFileRecord(
            customer = customer,
            tour = tour,
            agt = agt,
            paxCount = 2,
            departureDate = LocalDate.now().plusDays(30),
            returnDate = LocalDate.now().plusDays(35),
            fileCode = "TEST001"
        )
        
        // Then
        assert(result == fileRecord)
        verify { fileRecordRepository.save(any()) }
    }
}