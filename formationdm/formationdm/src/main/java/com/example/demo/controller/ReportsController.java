
 package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

 @Controller
 @RequestMapping("/reports")
 public class ReportsController {
     @GetMapping
     public String showReports(Model model) {
         model.addAttribute("pageTitle", "Rapports et Statistiques");
         return "reports";
     }
 }