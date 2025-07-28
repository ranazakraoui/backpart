package com.example.demo.controller;

import com.example.demo.dto.DemandeFormationDTO;
import com.example.demo.dto.FormationDTO;
import com.example.demo.service.DemandeFormationService;
import com.example.demo.service.FormationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "http://localhost:4200")
public class DemandeFormationApiController {

    private final DemandeFormationService demandeFormationService;
    private final FormationService formationService;

    public DemandeFormationApiController(DemandeFormationService demandeFormationService,
                                         FormationService formationService) {
        this.demandeFormationService = demandeFormationService;
        this.formationService = formationService;
    }

    private Pageable getDefaultPageable() {
        return PageRequest.of(0, 20);
    }

    @GetMapping("/formulaire/{formationId}")
    public ResponseEntity<DemandeFormationDTO> getFormulaireWithFormation(@PathVariable Long formationId) {
        FormationDTO formation = formationService.getFormationById(formationId);
        DemandeFormationDTO dto = new DemandeFormationDTO();
        dto.setFormationId(formation.getId());
        dto.setFormationTitre(formation.getTitre());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<DemandeFormationDTO>> listDemandes() {
        try {
            List<DemandeFormationDTO> demandes = demandeFormationService.getAllDemandesWithFormation();
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/formations/demandes-count")
    public ResponseEntity<Map<Long, Long>> getDemandesParFormation() {
        Map<Long, Long> result = demandeFormationService.countAllDemandesByFormation();
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        try {
            List<String> statutsValides = List.of("EN_ATTENTE", "APPROUVEE", "REJETEE");
            if (!statutsValides.contains(statut.toUpperCase())) {
                return ResponseEntity.badRequest().body("Statut invalide : " + statut);
            }

            DemandeFormationDTO demande = demandeFormationService.getDemandeById(id);
            demande.setStatut(statut.toUpperCase());
            demandeFormationService.updateDemande(id, demande);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la mise à jour du statut : " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createDemande(@RequestBody @Valid DemandeFormationDTO demandeDTO,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        FormationDTO formation = formationService.getFormationById(demandeDTO.getFormationId());
        if (formation == null) {
            return ResponseEntity.badRequest().body("Formation introuvable");
        }

        try {
            DemandeFormationDTO savedDemande = demandeFormationService.createDemande(demandeDTO);
            return ResponseEntity.ok(savedDemande);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur: " + e.getMessage());
        }
    }
}