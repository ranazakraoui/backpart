package com.example.demo.controller;

import com.example.demo.service.DemandeFormationService;
import com.example.demo.service.FormationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
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
        addGlobalStatistics(model);
        addDemandStatistics(model);
        addFormationStatistics(model);
        return "dashboard";
    }

    private void addGlobalStatistics(Model model) {
        model.addAttribute("pageTitle", "Tableau de Bord");
        model.addAttribute("userCount", demandeFormationService.countDistinctUsers());
        model.addAttribute("formationCount", formationService.countAllFormations());
        model.addAttribute("activeFormations", formationService.countActiveFormations());
        model.addAttribute("avgDemandsPerFormation", formationService.getAverageDemandsPerFormation());
    }

    private void addDemandStatistics(Model model) {
        model.addAttribute("totalDemandes", demandeFormationService.countAllDemandes());
        model.addAttribute("demandesEnAttente", demandeFormationService.countByStatut("EN_ATTENTE"));
        model.addAttribute("demandesApprouvees", demandeFormationService.countByStatut("APPROUVEE"));
        model.addAttribute("demandesRejetees", demandeFormationService.countByStatut("REJETEE"));
        model.addAttribute("evolutionMensuelle", demandeFormationService.getMonthlyEvolution());
    }

    private void addFormationStatistics(Model model) {
        Map<String, Long> demandesParFormation = formationService.getDemandesCountByFormationTitle();
        Map<String, Map<String, Long>> statutsParFormation = formationService.getStatutsCountByFormationTitle();

        List<Map.Entry<String, Long>> topFormations = demandesParFormation.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        Map<String, Long> motsCles = formationService.analyzeTitleKeywords();
        Map<String, Double> successRateByFormation = formationService.getSuccessRateByFormationTitle();

        model.addAttribute("demandesParFormation", demandesParFormation);
        model.addAttribute("statutsParFormation", statutsParFormation);
        model.addAttribute("topFormations", topFormations);
        model.addAttribute("motsClesTitres", motsCles);
        model.addAttribute("successRates", successRateByFormation);
        model.addAttribute("titleLengthAnalysis", formationService.analyzeTitleLengthImpact());
    }
}