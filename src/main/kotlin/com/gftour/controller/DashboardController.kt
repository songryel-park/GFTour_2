package com.gftour.controller

import com.gftour.entity.*
import com.gftour.service.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 시스템 메인 정보 REST 컨트롤러
 * 대시보드 및 통계 정보 API
 */
@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val fileRecordService: FileRecordService,
    private val documentService: DocumentService,
    private val agtService: AGTService,
    private val financialService: FinancialService,
    private val customerService: CustomerService,
    private val guideService: GuideService
) {
    
    /**
     * 대시보드 통계 조회
     */
    @GetMapping("/statistics")
    fun getDashboardStatistics(): ResponseEntity<DashboardStatistics> {
        val totalCustomers = customerService.getAllCustomers().size
        val vipCustomers = customerService.getVipCustomers().size
        val activeAGTs = agtService.getActiveAGTs().size
        val pendingDocuments = documentService.getDocumentsByStatus(DocumentStatus.PENDING_APPROVAL).size
        val financialSummary = financialService.getFinancialSummary()
        
        val statistics = DashboardStatistics(
            totalCustomers = totalCustomers,
            vipCustomers = vipCustomers,
            activeAGTs = activeAGTs,
            pendingDocuments = pendingDocuments,
            totalRevenue = financialSummary.totalReceived,
            totalProfit = financialSummary.totalSubTotal,
            unpaidAmount = financialSummary.totalUnpaid
        )
        
        return ResponseEntity.ok(statistics)
    }
    
    /**
     * 최근 파일 레코드 조회
     */
    @GetMapping("/recent-files")
    fun getRecentFiles(): ResponseEntity<List<FileRecord>> {
        // TODO: 최근 생성된 파일 레코드 조회 로직 구현
        return ResponseEntity.ok(emptyList())
    }
    
    /**
     * 시스템 정보 조회
     */
    @GetMapping("/system-info")
    fun getSystemInfo(): ResponseEntity<SystemInfo> {
        val systemInfo = SystemInfo(
            appName = "Good Feel Tour 업무 관리 시스템",
            version = "1.0.0",
            description = "여행사 업무 관리를 위한 통합 시스템",
            menus = listOf(
                "File 검색",
                "신규등록", 
                "AGT 관리",
                "고객명단",
                "단체행동지침서",
                "정산보고서"
            )
        )
        
        return ResponseEntity.ok(systemInfo)
    }
}

/**
 * 대시보드 통계 정보
 */
data class DashboardStatistics(
    val totalCustomers: Int,
    val vipCustomers: Int,
    val activeAGTs: Int,
    val pendingDocuments: Int,
    val totalRevenue: java.math.BigDecimal,
    val totalProfit: java.math.BigDecimal,
    val unpaidAmount: java.math.BigDecimal
)

/**
 * 시스템 정보
 */
data class SystemInfo(
    val appName: String,
    val version: String,
    val description: String,
    val menus: List<String>
)