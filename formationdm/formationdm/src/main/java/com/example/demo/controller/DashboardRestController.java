package com.example.demo.controller;

import com.example.demo.service.DemandeFormationService;
import com.example.demo.service.FormationService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200") // Autorise les requêtes Angular
public class DashboardRestController {

    private final DemandeFormationService demandeFormationService;
    private final FormationService formationService;

    public DashboardRestController(DemandeFormationService demandeFormationService,
                                   FormationService formationService) {
        this.demandeFormationService = demandeFormationService;
        this.formationService = formationService;
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("formations", formationService.countAllFormations());
        stats.put("demandesTotal", demandeFormationService.countByStatut(null));
        stats.put("demandesEnAttente", demandeFormationService.countByStatut("EN_ATTENTE"));
        stats.put("demandesApprouvees", demandeFormationService.countByStatut("APPROUVEE"));
        stats.put("demandesRejetees", demandeFormationService.countByStatut("REJETEE"));
        return stats;
    }
}
