package com.example.demo.controller;

import com.example.demo.dto.DemandeFormationDTO;
import com.example.demo.dto.FormationDTO;
import com.example.demo.model.Formation;
import com.example.demo.repository.DemandeFormationRepository;
import com.example.demo.service.DemandeFormationService;
import com.example.demo.service.FormationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "http://localhost:4200")
public class DemandeFormationApiController {

    private final DemandeFormationService demandeFormationService;
    private final FormationService formationService;
    private final DemandeFormationRepository demandeFormationRepository;

    public DemandeFormationApiController(DemandeFormationService demandeFormationService,
                                         FormationService formationService,
                                         DemandeFormationRepository demandeFormationRepository) {
        this.demandeFormationService = demandeFormationService;
        this.formationService = formationService;
        this.demandeFormationRepository = demandeFormationRepository;
    }

    private Pageable getDefaultPageable() {
        return PageRequest.of(0, 20); // Réduit à 20 pour de meilleures performances
    }

    @GetMapping("/formulaire/{formationId}")
    public ResponseEntity<DemandeFormationDTO> getFormulaireWithFormation(@PathVariable Long formationId) {
        Formation formation = formationService.getFormationEntityById(formationId);
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
            return ResponseEntity.internalServerError().body(null);
        }
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

    @PatchMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        try {
            // Liste des statuts valides
            List<String> statutsValides = List.of("EN_ATTENTE", "APPROUVÉE", "REJETÉE");
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

        // Validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // Vérification formation
        FormationDTO formation = formationService.getFormationById(demandeDTO.getFormationId());
        if (formation == null) {
            return ResponseEntity.badRequest().body("Formation introuvable");
        }
        if (!formation.isPlanifiee()) {
            return ResponseEntity.badRequest().body("La formation n'est pas encore planifiée");
        }

        try {
            demandeDTO.setStatut("EN_ATTENTE");
            demandeDTO.setDateDemande(new Date());

            // Assurez-vous que createDemande retourne le DTO sauvegardé
            DemandeFormationDTO savedDemande = demandeFormationService.createDemande(demandeDTO);
            return ResponseEntity.ok(savedDemande);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur: " + e.getMessage());
        }
    }








}