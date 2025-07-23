package com.example.demo.controller;

import com.example.demo.dto.FormationDTO;
import com.example.demo.service.FormationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin(origins = "http://localhost:4200") // ✅ CORS pour Angular
public class FormationController {

    private final FormationService formationService;
    private static final int DEFAULT_PAGE_SIZE = 10;

    public FormationController(FormationService formationService) {
        this.formationService = formationService;
    }

    // ===== PARTIE VUE THYMELEAF =====
    @GetMapping("/formations")
    public String listFormations(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String titre,
                                 @RequestParam(required = false) Boolean planifiee,
                                 @RequestParam(required = false) Integer minDuree,
                                 @RequestParam(required = false) Integer maxDuree,
                                 @RequestParam(defaultValue = "id") String sortBy,
                                 @RequestParam(defaultValue = "asc") String direction) {

        Page<FormationDTO> formationsPage = formationService.getFilteredFormationsPaginated(
                page, size, titre, planifiee, minDuree, maxDuree, sortBy, direction);

        model.addAttribute("formations", formationsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", formationsPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("titre", titre);
        model.addAttribute("planifiee", planifiee);
        model.addAttribute("minDuree", minDuree);
        model.addAttribute("maxDuree", maxDuree);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        return "formation/formations";
    }

    @GetMapping("/formations/new")
    public String showCreateForm(Model model) {
        model.addAttribute("formation", new FormationDTO());
        return "formation/add-formation";
    }

    @GetMapping("/formations/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        FormationDTO formation = formationService.getFormationById(id);
        model.addAttribute("formation", formation);
        model.addAttribute("pageTitle", "Modifier Formation");
        return "formation/edit-formation";
    }

    @GetMapping("/formations/{id}")
    public String viewFormation(@PathVariable Long id, Model model) {
        FormationDTO formation = formationService.getFormationWithDemandCount(id);
        model.addAttribute("formation", formation);
        return "formation/view-details";
    }

    @PostMapping("/formations/save")
    public String saveFormation(@ModelAttribute FormationDTO formationDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            FormationDTO savedFormation = formationService.saveFormation(formationDTO);
            redirectAttributes.addFlashAttribute("success", "Formation enregistrée avec succès!");
            return "redirect:/formations/" + savedFormation.getId();
        } catch (Exception e) {
            handleFormationError(redirectAttributes, e, "l'enregistrement");
            return "redirect:/formations/new";
        }
    }

    @PostMapping("/formations/update/{id}")
    public String updateFormation(@PathVariable Long id,
                                  @ModelAttribute FormationDTO formationDTO,
                                  RedirectAttributes redirectAttributes) {
        try {
            formationDTO.setId(id);
            formationService.updateFormation(formationDTO);
            redirectAttributes.addFlashAttribute("success", "Formation mise à jour avec succès!");
            return "redirect:/formations/" + id;
        } catch (Exception e) {
            handleFormationError(redirectAttributes, e, "la mise à jour");
            return "redirect:/formations/edit/" + id;
        }
    }

    @PostMapping("/formations/delete/{id}")
    public String deleteFormation(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            formationService.deleteFormation(id);
            redirectAttributes.addFlashAttribute("success", "Formation supprimée avec succès!");
        } catch (Exception e) {
            handleFormationError(redirectAttributes, e, "la suppression");
        }
        return "redirect:/formations";
    }

    // ===== PARTIE API REST POUR ANGULAR =====

    // ✅ ENDPOINT PRINCIPAL : GET /api/formations
    @GetMapping("/api/formations")
    @ResponseBody
    public ResponseEntity<List<FormationDTO>> getAllFormationsApi() {
        return ResponseEntity.ok(formationService.findAll());
    }

    @GetMapping("/api/formations/paginated")
    @ResponseBody
    public ResponseEntity<Page<FormationDTO>> getAllFormationsPaginatedApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(formationService.getAllFormationsPaginated(page, size));
    }

    @GetMapping("/api/formations/filtered-paginated")
    @ResponseBody
    public ResponseEntity<Page<FormationDTO>> getFilteredFormationsPaginatedApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) Boolean planifiee,
            @RequestParam(required = false) Integer minDuree,
            @RequestParam(required = false) Integer maxDuree,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<FormationDTO> pageResult = formationService.getFilteredFormationsPaginated(
                page, size, titre, planifiee, minDuree, maxDuree, sortBy, direction);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("/api/formations/{id}")
    @ResponseBody
    public ResponseEntity<FormationDTO> getFormationApi(@PathVariable Long id) {
        return ResponseEntity.ok(formationService.getFormationById(id));
    }

    @PostMapping("/api/formations")
    @ResponseBody
    public ResponseEntity<FormationDTO> createFormationApi(@RequestBody FormationDTO formationDTO) {
        try {
            FormationDTO savedFormation = formationService.saveFormation(formationDTO);
            return ResponseEntity.ok(savedFormation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/formations/sorted")
    @ResponseBody
    public ResponseEntity<List<FormationDTO>> getFormationsSorted(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(formationService.getAllFormationsSorted(sortBy, direction));
    }

    @GetMapping("/api/formations/filtered")
    @ResponseBody
    public ResponseEntity<List<FormationDTO>> getFilteredFormations(
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) Boolean planifiee,
            @RequestParam(required = false) Integer minDuree,
            @RequestParam(required = false) Integer maxDuree,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        List<FormationDTO> formations = formationService.getFilteredFormations(
                titre, planifiee, minDuree, maxDuree, sortBy, direction);
        return ResponseEntity.ok(formations);
    }

    @PutMapping("/api/formations/{id}")
    @ResponseBody
    public ResponseEntity<FormationDTO> updateFormationApi(@PathVariable Long id,
                                                           @RequestBody FormationDTO formationDTO) {
        try {
            formationDTO.setId(id);
            FormationDTO updatedFormation = formationService.updateFormation(formationDTO);
            return ResponseEntity.ok(updatedFormation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/formations/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getFormationStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalFormations", formationService.countAllFormations());
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/api/formations/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteFormationApi(@PathVariable Long id) {
        try {
            formationService.deleteFormation(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/formations/{id}/disponible")
    @ResponseBody
    public boolean checkFormationDisponible(@PathVariable Long id) {
        return formationService.isFormationDisponible(id);
    }
    // ===== MÉTHODES COMMUNES =====
    private void handleFormationError(RedirectAttributes redirectAttributes, Exception e, String operation) {
        redirectAttributes.addFlashAttribute("error",
                "Erreur lors de " + operation + ": " + e.getMessage());
    }
}