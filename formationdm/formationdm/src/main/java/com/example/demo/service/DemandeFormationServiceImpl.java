package com.example.demo.service;

import com.example.demo.dto.DemandeFormationDTO;
import com.example.demo.dto.FormationDTO;
import com.example.demo.dto.FormationWithDemandCountDTO;
import com.example.demo.model.DemandeFormation;
import com.example.demo.model.Formation;
import com.example.demo.repository.DemandeFormationRepository;
import com.example.demo.repository.FormationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DemandeFormationServiceImpl implements DemandeFormationService {

    private final DemandeFormationRepository demandeFormationRepository;
    private final FormationRepository formationRepository;

    public DemandeFormationServiceImpl(DemandeFormationRepository demandeFormationRepository,
                                       FormationRepository formationRepository) {
        this.demandeFormationRepository = demandeFormationRepository;
        this.formationRepository = formationRepository;
    }

    @Override
    public DemandeFormationDTO saveDemande(DemandeFormationDTO demandeDTO) {
        Formation formation = formationRepository.findById(demandeDTO.getFormationId())
                .orElseThrow(() -> new NoSuchElementException("Formation non trouvée avec l'ID: " + demandeDTO.getFormationId()));

        DemandeFormation demande = new DemandeFormation();
        demande.setDateDemande(new Date());
        demande.setEmailCollaborateur(demandeDTO.getEmailCollaborateur());
        demande.setNomCollaborateur(demandeDTO.getNomCollaborateur());
        demande.setStatut("EN_ATTENTE");
        demande.setFormation(formation);

        return convertToDto(demandeFormationRepository.save(demande));
    }





    @Override
    public List<DemandeFormationDTO> getAllDemandes() {
        return demandeFormationRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public long countByStatut(String statut) {
        return statut == null ?
                demandeFormationRepository.count() :
                demandeFormationRepository.countByStatut(statut);
    }

    @Override
    public long countByFormationId(Long formationId) {
        return demandeFormationRepository.countByFormationId(formationId);
    }

    @Override
    public Map<Long, Long> countAllDemandesByFormation() {
        return demandeFormationRepository.countAllByFormation()
                .stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (Long) result[1]
                ));
    }

    @Override
    public List<FormationWithDemandCountDTO> getTopFormationsWithDemandCount() {
        try {
            List<Object[]> results = demandeFormationRepository.findTopFormationsWithDemandCount();
            return results.stream()
                    .filter(Objects::nonNull)
                    .filter(result -> result.length >= 3)
                    .map(result -> {
                        Long id = result[0] != null ? ((Number) result[0]).longValue() : null;
                        String titre = result[1] != null ? (String) result[1] : "Inconnu";
                        Long count = result[2] != null ? ((Number) result[2]).longValue() : 0L;
                        return new FormationWithDemandCountDTO(id, titre, count);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des formations", e);
        }
    }
    @Override
    public void createDemande(DemandeFormationDTO demande) {
        this.saveDemande(demande);
    }

    @Override
    public List<DemandeFormationDTO> getDemandesByFormationId(Long formationId) {
        return demandeFormationRepository.findByFormationId(formationId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DemandeFormationDTO> getAllDemandesWithFormation() {
        return demandeFormationRepository.findAll().stream()
                .map(demande -> {
                    DemandeFormationDTO dto = convertToDto(demande);

                    // Créez un FormationDTO complet
                    Formation formation = demande.getFormation();
                    FormationDTO formationDTO = new FormationDTO();
                    formationDTO.setId(formation.getId());
                    formationDTO.setTitre(formation.getTitre());
                    formationDTO.setDescription(formation.getDescription());
                    formationDTO.setDuree(formation.getDuree());
                    formationDTO.setPlanifiee(formation.isPlanifiee());

                    dto.setFormation(formationDTO);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private DemandeFormationDTO convertToDto(DemandeFormation demande) {
        DemandeFormationDTO dto = new DemandeFormationDTO();
        dto.setId(demande.getId());
        dto.setDateDemande(demande.getDateDemande());
        dto.setEmailCollaborateur(demande.getEmailCollaborateur());
        dto.setNomCollaborateur(demande.getNomCollaborateur());
        dto.setStatut(demande.getStatut());
        dto.setFormationId(demande.getFormation().getId());
        dto.setFormationTitre(demande.getFormation().getTitre());
        return dto;
    }

    private DemandeFormationDTO convertToDtoWithFormation(DemandeFormation demande) {
        return convertToDto(demande); // Même implémentation pour l'instant
    }



    @Override
    public DemandeFormationDTO updateDemande(Long id, DemandeFormationDTO demandeDTO) {
        // 1. Récupération de la demande existante
        DemandeFormation demande = demandeFormationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Demande non trouvée avec l'ID: " + id));

        // 2. Validation de la formation associée
        Formation formation = formationRepository.findById(demandeDTO.getFormationId())
                .orElseThrow(() -> new NoSuchElementException("Formation non trouvée avec l'ID: " + demandeDTO.getFormationId()));

        // 3. Mise à jour des champs
        demande.setEmailCollaborateur(demandeDTO.getEmailCollaborateur());
        demande.setNomCollaborateur(demandeDTO.getNomCollaborateur());
        demande.setStatut(demandeDTO.getStatut());
        demande.setFormation(formation);

        // 4. Logique de planification automatique (nouveau)
        if ("APPROUVEE".equals(demandeDTO.getStatut())) {
            long approvedCount = demandeFormationRepository.countByFormationIdAndStatut(
                    formation.getId(),
                    "APPROUVEE"
            );

            if (approvedCount >= 5) { // Seuil configurable
                formation.setPlanifiee(true);
                formationRepository.save(formation); // Sauvegarde de la mise à jour
            }
        }

        // 5. Sauvegarde et retour
        return convertToDto(demandeFormationRepository.save(demande));
    }

    @Override
    public void deleteDemande(Long id) {
        if (!demandeFormationRepository.existsById(id)) {
            throw new NoSuchElementException("Demande non trouvée avec l'ID: " + id);
        }
        demandeFormationRepository.deleteById(id);
    }

    @Override
    public DemandeFormationDTO getDemandeById(Long id) {
        return demandeFormationRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NoSuchElementException("Demande non trouvée avec l'ID: " + id));
    }






}