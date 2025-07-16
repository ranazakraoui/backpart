package com.example.demo.service;

import com.example.demo.dto.DemandeFormationDTO;
import com.example.demo.dto.FormationWithDemandCountDTO;
import java.util.List;
import java.util.Map;

public interface DemandeFormationService {
    DemandeFormationDTO saveDemande(DemandeFormationDTO demandeDTO);
    DemandeFormationDTO updateDemande(Long id, DemandeFormationDTO demandeDTO);
    void deleteDemande(Long id);
    DemandeFormationDTO getDemandeById(Long id);
    List<DemandeFormationDTO> getAllDemandes();
    long countByStatut(String statut);
    long countByFormationId(Long formationId);
    Map<Long, Long> countAllDemandesByFormation();
    List<FormationWithDemandCountDTO> getTopFormationsWithDemandCount();
    void createDemande(DemandeFormationDTO demande);
    List<DemandeFormationDTO> getDemandesByFormationId(Long formationId);
    List<DemandeFormationDTO> getAllDemandesWithFormation();
}