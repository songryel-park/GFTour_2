package com.gftour.system.controller

import com.gftour.system.service.FileRecordService
import com.gftour.system.service.CustomerService
import com.gftour.system.service.AGTService
import com.gftour.system.service.FinancialService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainController(
    private val fileRecordService: FileRecordService,
    private val customerService: CustomerService,
    private val agtService: AGTService,
    private val financialService: FinancialService
) {

    @GetMapping("/")
    fun home(): String {
        return "redirect:/dashboard"
    }

    @GetMapping("/login")
    fun loginPage(): String {
        return "auth/login"
    }

    @GetMapping("/register")
    fun registerPage(): String {
        return "auth/register"
    }

    @GetMapping("/dashboard")
    fun dashboard(model: Model): String {
        model.addAttribute("pageTitle", "대시보드")
        
        // Add dashboard statistics
        try {
            val totalFiles = fileRecordService.getTotalFileCount()
            val totalCustomers = customerService.getTotalCustomerCount()
            val totalAgts = agtService.getTotalAgtCount()
            val monthlySettlement = financialService.getMonthlySettlement()
            val recentFiles = fileRecordService.getRecentFiles(5)
            
            model.addAttribute("totalFiles", totalFiles)
            model.addAttribute("totalCustomers", totalCustomers)
            model.addAttribute("totalAgts", totalAgts)
            model.addAttribute("monthlySettlement", monthlySettlement)
            model.addAttribute("recentFiles", recentFiles)
        } catch (e: Exception) {
            // Fallback to default values if services fail
            model.addAttribute("totalFiles", 0)
            model.addAttribute("totalCustomers", 0)
            model.addAttribute("totalAgts", 0)
            model.addAttribute("monthlySettlement", "₩0")
            model.addAttribute("recentFiles", emptyList<Any>())
        }
        
        return "dashboard"
    }

    @GetMapping("/files")
    fun fileSearch(model: Model): String {
        model.addAttribute("pageTitle", "파일 검색")
        return "files/search"
    }

    @GetMapping("/files/new")
    fun newRegistration(model: Model): String {
        model.addAttribute("pageTitle", "신규 등록")
        return "files/form"
    }

    @GetMapping("/agts")
    fun agtManagement(model: Model): String {
        model.addAttribute("pageTitle", "AGT 관리")
        return "agts/list"
    }

    @GetMapping("/customers")
    fun customerList(model: Model): String {
        model.addAttribute("pageTitle", "고객 명단")
        return "customers/list"
    }

    @GetMapping("/guides")
    fun guideInstructions(model: Model): String {
        model.addAttribute("pageTitle", "단체행동지침서")
        return "guides/instructions"
    }

    @GetMapping("/financial")
    fun settlementReports(model: Model): String {
        model.addAttribute("pageTitle", "정산보고서")
        return "financial/reports"
    }

    @GetMapping("/documents")
    fun documentManagement(model: Model): String {
        model.addAttribute("pageTitle", "문서 관리")
        return "documents/list"
    }

    @GetMapping("/documents/generator")
    fun documentGenerator(model: Model): String {
        model.addAttribute("pageTitle", "문서 생성기")
        return "documents/generator"
    }
}