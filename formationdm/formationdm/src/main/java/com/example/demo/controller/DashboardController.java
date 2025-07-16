package com.example.demo.controller;

import com.example.demo.service.DemandeFormationService;
import com.example.demo.service.FormationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final DemandeFormationService demandeFormationService;
    private final FormationService formationService;

    public DashboardController(DemandeFormationService demandeFormationService,
                               FormationService formationService) {
        this.demandeFormationService = demandeFormationService;
        this.formationService = formationService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Stats principales
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("userCount", 245);
        model.addAttribute("formationCount", formationService.countAllFormations());
        model.addAttribute("orderCount", 156);
        model.addAttribute("revenue", "12,345");

        // Stats des demandes de formation
        model.addAttribute("totalDemandes", demandeFormationService.countByStatut(null));
        model.addAttribute("demandesEnAttente", demandeFormationService.countByStatut("EN_ATTENTE"));
        model.addAttribute("demandesApprouvees", demandeFormationService.countByStatut("APPROUVEE"));
        model.addAttribute("demandesRejetees", demandeFormationService.countByStatut("REJETEE"));

        // Top formations (version corrigée)
        model.addAttribute("topFormations", formationService.getFormationsWithDemandCount()
                .stream()
                .limit(5)
                .collect(Collectors.toList()));

        return "dashboard";
    }





}