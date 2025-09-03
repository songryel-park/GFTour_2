package com.gftour.controller

import com.gftour.entity.*
import com.gftour.service.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * 파일 레코드 REST 컨트롤러
 * 신규등록 관리 API
 */
@RestController
@RequestMapping("/api/file-records")
class FileRecordController(
    private val fileRecordService: FileRecordService,
    private val customerService: CustomerService,
    private val agtService: AGTService
) {
    
    /**
     * REF No 생성
     */
    @GetMapping("/generate-ref-no")
    fun generateRefNo(): ResponseEntity<Map<String, String>> {
        val refNo = fileRecordService.generateRefNo()
        return ResponseEntity.ok(mapOf("refNo" to refNo))
    }
    
    /**
     * 파일 레코드 생성
     */
    @PostMapping
    fun createFileRecord(@RequestBody request: CreateFileRecordRequest): ResponseEntity<FileRecord> {
        val customer = customerService.findById(request.customerId) 
            ?: return ResponseEntity.badRequest().build()
        
        val tour = Tour(
            id = request.tourId,
            name = request.tourName ?: "기본 투어",
            startDate = request.departureDate,
            endDate = request.returnDate,
            price = request.tourPrice ?: java.math.BigDecimal.ZERO
        )
        
        val agt = agtService.findById(request.agtId) 
            ?: return ResponseEntity.badRequest().build()
        
        val fileRecord = fileRecordService.createFileRecord(
            customer = customer,
            tour = tour,
            agt = agt,
            paxCount = request.paxCount,
            departureDate = request.departureDate,
            returnDate = request.returnDate,
            fileCode = request.fileCode,
            remarks = request.remarks
        )
        
        return ResponseEntity.ok(fileRecord)
    }
    
    /**
     * 파일 레코드 검색
     */
    @GetMapping("/search")
    fun searchFileRecords(
        @RequestParam(required = false) refNo: String?,
        @RequestParam(required = false) customerName: String?,
        @RequestParam(required = false) status: FileStatus?,
        pageable: Pageable
    ): ResponseEntity<Page<FileRecord>> {
        val result = fileRecordService.searchFileRecords(refNo, customerName, status, pageable)
        return ResponseEntity.ok(result)
    }
    
    /**
     * 파일 레코드 상세 조회
     */
    @GetMapping("/{refNo}")
    fun getFileRecord(@PathVariable refNo: String): ResponseEntity<FileRecord> {
        val fileRecord = fileRecordService.findByRefNo(refNo)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(fileRecord)
    }
    
    /**
     * 파일 레코드 상태 업데이트
     */
    @PutMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<FileRecord> {
        val updated = fileRecordService.updateStatus(id, request.status)
        return ResponseEntity.ok(updated)
    }
}

/**
 * 파일 레코드 생성 요청
 */
data class CreateFileRecordRequest(
    val customerId: Long,
    val tourId: Long = 0,
    val tourName: String? = null,
    val tourPrice: java.math.BigDecimal? = null,
    val agtId: Long,
    val paxCount: Int,
    val departureDate: LocalDate,
    val returnDate: LocalDate,
    val fileCode: String,
    val remarks: String? = null
)

/**
 * 상태 업데이트 요청
 */
data class UpdateStatusRequest(
    val status: FileStatus
)