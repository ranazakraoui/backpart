package com.example.demo.service;

import com.example.demo.dto.DemandeFormationDTO;
import com.example.demo.dto.FormationWithDemandCountDTO;
import java.util.List;
import java.util.Map;

public interface DemandeFormationService {
    // Méthodes principales
    DemandeFormationDTO createDemande(DemandeFormationDTO demandeDTO);
    DemandeFormationDTO updateDemande(Long id, DemandeFormationDTO demandeDTO);
    void deleteDemande(Long id);

    // Méthodes de lecture
    DemandeFormationDTO getDemandeById(Long id);
    List<DemandeFormationDTO> getAllDemandes();
    List<DemandeFormationDTO> getDemandesByFormationId(Long formationId);
    List<DemandeFormationDTO> getAllDemandesWithFormation();

    // Méthodes de statistiques
    long countByStatut(String statut);
    long countByFormationId(Long formationId);
    Map<Long, Long> countAllDemandesByFormation();
    List<FormationWithDemandCountDTO> getTopFormationsWithDemandCount();

    // Méthode dépréciée (à supprimer à terme)
    @Deprecated
    DemandeFormationDTO saveDemande(DemandeFormationDTO demandeDTO);
}