package com.example.demo.controller;

import com.example.demo.dto.DemandeFormationDTO;
import com.example.demo.model.Formation;
import com.example.demo.repository.DemandeFormationRepository;
import com.example.demo.service.DemandeFormationService;
import com.example.demo.service.FormationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/demandes")
public class DemandeFormationController {

    private final DemandeFormationService demandeFormationService;
    private final FormationService formationService;
    private final DemandeFormationRepository demandeFormationRepository;

    public DemandeFormationController(DemandeFormationService demandeFormationService,
                                      FormationService formationService,
                                      DemandeFormationRepository demandeFormationRepository) {
        this.demandeFormationService = demandeFormationService;
        this.formationService = formationService;
        this.demandeFormationRepository = demandeFormationRepository;
    }

    // Ajoutez cette méthode pour la pagination par défaut
    private Pageable getDefaultPageable() {
        return PageRequest.of(0, 100); // 100 éléments par défaut
    }
    @GetMapping("/api/formulaire/{formationId}")
    public ResponseEntity<DemandeFormationDTO> getFormulaireWithFormation(
            @PathVariable Long formationId) {
// APRÈS (utilise la nouvelle méthode qui retourne l'entité)
        Formation formation = formationService.getFormationEntityById(formationId);
        DemandeFormationDTO dto = new DemandeFormationDTO();
        dto.setFormationId(formation.getId());
        dto.setFormationTitre(formation.getTitre());
        return ResponseEntity.ok(dto);
    }
    @GetMapping
    public String listDemandes(Model model) {
        try {
            List<DemandeFormationDTO> demandes = demandeFormationService.getAllDemandesWithFormation();
            model.addAttribute("demandes", demandes);
            return "demandes/list";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement: " + e.getMessage());
            return "demandes/list";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("demande", new DemandeFormationDTO());
        model.addAttribute("formations", formationService.findAll()); // Utilisez findAll() au lieu de getAllFormations()
        return "demandes/add";
    }

    @PostMapping("/save")
    public String saveDemande(@Valid @ModelAttribute("demande") DemandeFormationDTO demande,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("formations", formationService.findAll());
            return "demandes/add";
        }

        try {
            demande.setStatut("EN_ATTENTE");
            demande.setDateDemande(new Date());
            demandeFormationService.createDemande(demande);
            redirectAttributes.addFlashAttribute("success", "Demande créée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            model.addAttribute("formations", formationService.findAll());
            return "demandes/add";
        }

        return "redirect:/demandes";
    }

    @GetMapping("/formations/demandes-count")
    public ResponseEntity<Map<Long, Long>> getDemandesParFormation() {
        List<Object[]> counts = demandeFormationRepository.countAllByFormation();
        Map<Long, Long> result = new HashMap<>();
        for (Object[] row : counts) {
            result.put((Long) row[0], (Long) row[1]);
        }
        return ResponseEntity.ok(result);
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

    @PatchMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(
            @PathVariable Long id,
            @RequestParam String statut) {

        try {
            DemandeFormationDTO demande = demandeFormationService.getDemandeById(id);
            demande.setStatut(statut); // Validez le statut ici !
            demandeFormationService.updateDemande(id, demande);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }






}