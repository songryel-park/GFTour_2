package com.gftour.system.controller

import com.gftour.system.dto.*
import com.gftour.system.service.FinancialService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/financial")
@Tag(name = "Financial", description = "정산 관리 API - 정산보고서, 수수료 계산, 영수증")
class FinancialController(
    private val financialService: FinancialService
) {
    
    @PostMapping("/records")
    @Operation(summary = "정산 정보 등록/수정", description = "파일의 정산 정보를 등록하거나 수정")
    fun createOrUpdateFinancialRecord(@Valid @RequestBody request: FinancialRecordRequest): ResponseEntity<ApiResponse<FinancialRecordDto>> {
        return try {
            val response = financialService.createOrUpdateFinancialRecord(request)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "정산 정보가 성공적으로 저장되었습니다",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "정산 정보 저장 실패"
                )
            )
        }
    }
    
    @GetMapping("/records/{fileId}")
    @Operation(summary = "정산 정보 조회", description = "특정 파일의 정산 정보 조회")
    fun getFinancialRecord(@PathVariable fileId: Long): ResponseEntity<ApiResponse<FinancialRecordDto?>> {
        return try {
            val response = financialService.getFinancialRecord(fileId)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "정산 정보 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "정산 정보 조회 실패"
                )
            )
        }
    }
    
    @GetMapping("/reports")
    @Operation(summary = "정산보고서", description = "전체 정산보고서 조회")
    fun getFinancialReports(): ResponseEntity<ApiResponse<List<FinancialRecordDto>>> {
        return try {
            val response = financialService.getFinancialReports()
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "정산보고서 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "정산보고서 조회 실패"
                )
            )
        }
    }
    
    @GetMapping("/reports/by-period")
    @Operation(summary = "기간별 업무 현황", description = "기간별 정산보고서 조회")
    fun getFinancialReportsByPeriod(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime
    ): ResponseEntity<ApiResponse<List<FinancialRecordDto>>> {
        return try {
            val response = financialService.getFinancialReportsByPeriod(startDate, endDate)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "기간별 정산보고서 조회 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "기간별 정산보고서 조회 실패"
                )
            )
        }
    }
    
    @GetMapping("/commission/{fileId}")
    @Operation(summary = "수수료 계산", description = "특정 파일의 수수료 계산")
    fun calculateCommission(@PathVariable fileId: Long): ResponseEntity<ApiResponse<FinancialCommissionDto>> {
        return try {
            val response = financialService.calculateCommissionForFile(fileId)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "수수료 계산 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "수수료 계산 실패"
                )
            )
        }
    }
    
    @GetMapping("/receipts/{fileId}")
    @Operation(summary = "관광영수증 출력", description = "관광영수증 데이터 생성")
    fun generateReceipt(@PathVariable fileId: Long): ResponseEntity<ApiResponse<FinancialReceiptDto>> {
        return try {
            val response = financialService.generateReceipt(fileId)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "관광영수증 생성 완료",
                    data = response
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "관광영수증 생성 실패"
                )
            )
        }
    }
    
    @GetMapping("/summary")
    @Operation(summary = "재무 요약 정보", description = "전체 재무 현황 요약")
    fun getFinancialSummary(): ResponseEntity<ApiResponse<Map<String, Any>>> {
        return try {
            val reports = financialService.getFinancialReports()
            
            val totalSales = reports.sumOf { it.salesAmount }
            val totalReceipts = reports.sumOf { it.receiptAmount }
            val totalOperatingCosts = reports.sumOf { it.operatingCost }
            val totalSubTotal = reports.sumOf { it.subTotal }
            val totalCommissions = reports.sumOf { it.commissionAmount ?: java.math.BigDecimal.ZERO }
            
            val summary = mapOf(
                "totalFiles" to reports.size,
                "totalSalesAmount" to totalSales,
                "totalReceiptAmount" to totalReceipts,
                "totalOperatingCost" to totalOperatingCosts,
                "totalSubTotal" to totalSubTotal,
                "totalCommissions" to totalCommissions,
                "netProfit" to totalSubTotal.subtract(totalCommissions)
            )
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "재무 요약 정보 조회 완료",
                    data = summary
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "재무 요약 정보 조회 실패"
                )
            )
        }
    }
}