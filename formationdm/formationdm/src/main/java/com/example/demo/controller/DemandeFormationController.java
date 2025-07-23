package com.example.demo.controller;

import com.example.demo.dto.DemandeFormationDTO;
import com.example.demo.dto.FormationDTO;
import com.example.demo.service.DemandeFormationService;
import com.example.demo.service.FormationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/demandes")
@CrossOrigin(origins = "http://localhost:4200")
public class DemandeFormationController {

    private final DemandeFormationService demandeFormationService;
    private final FormationService formationService;

    public DemandeFormationController(DemandeFormationService demandeFormationService,
                                      FormationService formationService) {
        this.demandeFormationService = demandeFormationService;
        this.formationService = formationService;
    }

    private Pageable getDefaultPageable() {
        return PageRequest.of(0, 20); // Réduit à 20 pour de meilleures performances
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("demande", new DemandeFormationDTO());
        model.addAttribute("formations", formationService.findAll());
        return "demandes/add";
    }

    @PostMapping("/save")
    public String saveDemande(@Valid @ModelAttribute("demande") DemandeFormationDTO demande,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        // Debug logging
        System.out.println("Démande reçue: " + demande);

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.toString()));
            model.addAttribute("formations", formationService.findAll());
            return "demandes/add";
        }

        FormationDTO formation = formationService.getFormationById(demande.getFormationId());
        System.out.println("Formation trouvée: " + formation);

        if (formation == null) {
            System.out.println("ERREUR: Formation non trouvée avec ID: " + demande.getFormationId());
            redirectAttributes.addFlashAttribute("error", "Formation introuvable");
        } else if (!formation.isPlanifiee()) {
            System.out.println("ERREUR: Formation non planifiée - ID: " + formation.getId());
            redirectAttributes.addFlashAttribute("error", "La formation n'est pas encore planifiée");
        } else {
            try {
                demande.setStatut("EN_ATTENTE");
                demande.setDateDemande(new Date());
                demandeFormationService.createDemande(demande);
                redirectAttributes.addFlashAttribute("success", "Demande créée avec succès !");
            } catch (Exception e) {
                System.out.println("ERREUR: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Erreur technique: " + e.getMessage());
            }
        }

        return "redirect:/demandes";
    }

    @GetMapping("/{id}")
    public String viewDemande(@PathVariable Long id, Model model) {
        DemandeFormationDTO demande = demandeFormationService.getDemandeById(id);
        model.addAttribute("demande", demande);
        return "demandes/view";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        DemandeFormationDTO demande = demandeFormationService.getDemandeById(id);
        model.addAttribute("demande", demande);
        model.addAttribute("formations", formationService.findAll());
        return "demandes/edit";
    }

    @PostMapping("/update/{id}")
    public String updateDemande(@PathVariable Long id,
                                @Valid @ModelAttribute("demande") DemandeFormationDTO demandeDTO,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formations", formationService.findAll());
            return "demandes/edit";
        }
        try {
            demandeFormationService.updateDemande(id, demandeDTO);
            redirectAttributes.addFlashAttribute("success", "Demande mise à jour avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
            return "redirect:/demandes/edit/" + id;
        }
        return "redirect:/demandes";
    }

    @GetMapping("/delete/{id}")
    public String deleteDemande(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            demandeFormationService.deleteDemande(id);
            redirectAttributes.addFlashAttribute("success", "Demande supprimée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/demandes";
    }

    // Ajout d'une méthode pour lister toutes les demandes (si nécessaire)
    @GetMapping
    public String listDemandes(Model model) {
        model.addAttribute("demandes", demandeFormationService.getAllDemandesWithFormation());
        return "demandes/list";
    }
}