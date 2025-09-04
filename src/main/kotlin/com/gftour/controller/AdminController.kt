package com.gftour.controller

import com.gftour.service.DocumentService
import com.gftour.service.UserService
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class AdminController(
    private val documentService: DocumentService,
    private val userService: UserService
) {

    @GetMapping("/dashboard")
    fun dashboard(model: Model, authentication: Authentication): String {
        val user = userService.findByEmail(authentication.name)
        
        // Dashboard statistics
        val totalDocuments = documentService.getTotalDocuments()
        val pendingApprovals = documentService.getPendingApprovalsCount()
        val totalUsers = userService.getTotalUsers()
        val activeAgents = userService.getActiveAgentsCount()
        
        // Recent documents
        val recentDocuments = documentService.getRecentDocuments(10)
        
        model.addAttribute("user", user)
        model.addAttribute("totalDocuments", totalDocuments)
        model.addAttribute("pendingApprovals", pendingApprovals)
        model.addAttribute("totalUsers", totalUsers)
        model.addAttribute("activeAgents", activeAgents)
        model.addAttribute("recentDocuments", recentDocuments)
        
        return "admin/dashboard"
    }

    @GetMapping("/users")
    fun users(model: Model, authentication: Authentication): String {
        val user = userService.findByEmail(authentication.name)
        val allUsers = userService.getAllUsers()
        
        model.addAttribute("user", user)
        model.addAttribute("users", allUsers)
        
        return "admin/users"
    }

    @GetMapping("/documents")
    fun documents(model: Model, authentication: Authentication): String {
        val user = userService.findByEmail(authentication.name)
        val allDocuments = documentService.getAllDocuments()
        
        model.addAttribute("user", user)
        model.addAttribute("documents", allDocuments)
        
        return "admin/documents"
    }

    @GetMapping("/templates")
    fun templates(model: Model, authentication: Authentication): String {
        val user = userService.findByEmail(authentication.name)
        val templates = documentService.getAllTemplates()
        
        model.addAttribute("user", user)
        model.addAttribute("templates", templates)
        
        return "admin/templates"
    }
}