package com.gftour.controller

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {

    @GetMapping("/")
    fun home(authentication: Authentication?): String {
        return if (authentication?.isAuthenticated == true) {
            val authorities = authentication.authorities.map { it.authority }
            when {
                authorities.contains("ROLE_ADMIN") -> "redirect:/admin/dashboard"
                authorities.contains("ROLE_MANAGER") -> "redirect:/manager/dashboard"
                authorities.contains("ROLE_AGENT") -> "redirect:/agent/dashboard"
                else -> "redirect:/viewer/dashboard"
            }
        } else {
            "redirect:/login"
        }
    }

    @GetMapping("/login")
    fun login(model: Model): String {
        return "auth/login"
    }
}