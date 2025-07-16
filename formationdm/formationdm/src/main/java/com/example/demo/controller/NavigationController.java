package com.example.demo.controller;

import com.example.demo.dto.DemandeFormationDTO;
import com.example.demo.dto.FormationDTO;
import com.example.demo.model.Formation;
import com.example.demo.service.DemandeFormationService;
import com.example.demo.service.FormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Controller
@RequestMapping("/app")
public class NavigationController {

    private final FormationService formationService;
    private final DemandeFormationService demandeFormationService;

    @Autowired
    public NavigationController(FormationService formationService,
                                DemandeFormationService demandeFormationService) {
        this.formationService = formationService;
        this.demandeFormationService = demandeFormationService;
    }

    @GetMapping("/formations")
    public String listFormations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<FormationDTO> formationPage = formationService.getAllFormationsPaginated(page, size);

        model.addAttribute("formations", formationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", formationPage.getTotalPages());
        model.addAttribute("pageSize", size);

        return "formation/formations";
    }

    @GetMapping("/demandes")
    public String listDemandes(Model model) {
        List<DemandeFormationDTO> demandes = demandeFormationService.getAllDemandes();
        model.addAttribute("demandes", demandes);
        return "demandes/list";
    }
}