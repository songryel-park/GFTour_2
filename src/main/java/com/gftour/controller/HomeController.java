package com.gftour.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/file-search")
    public String fileSearch() {
        return "file-search";
    }
    
    @GetMapping("/new-registration")
    public String newRegistration() {
        return "new-registration";
    }
    
    @GetMapping("/agt-management")
    public String agtManagement() {
        return "agt-management";
    }
    
    @GetMapping("/customer-list")
    public String customerList() {
        return "customer-list";
    }
    
    @GetMapping("/group-guidelines")
    public String groupGuidelines() {
        return "group-guidelines";
    }
    
    @GetMapping("/settlement-report")
    public String settlementReport() {
        return "settlement-report";
    }
}