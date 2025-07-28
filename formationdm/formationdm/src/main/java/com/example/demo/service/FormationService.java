package com.example.demo.service;

import com.example.demo.dto.FormationDTO;
import com.example.demo.model.Formation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface FormationService {
    // CRUD
    FormationDTO saveFormation(FormationDTO formationDTO);
    FormationDTO updateFormation(FormationDTO formationDTO);
    void deleteFormation(Long id);
    Page<FormationDTO> getAllFormationsPaginated(int page, int size);

    // Read
    FormationDTO getFormationById(Long id);
    List<FormationDTO> findAll();
    Formation getFormationEntityById(Long id);
    List<FormationDTO> getAllFormations();
    List<FormationDTO> getAllFormationsSorted(String sortBy, String direction);
    boolean isFormationDisponible(Long formationId);

    // Filtrage et pagination
    Page<FormationDTO> getFilteredFormationsPaginated(
            int page, int size,
            String titre, Boolean planifiee,
            Integer minDuree, Integer maxDuree,
            String sortBy, String direction);
    List<FormationDTO> getFilteredFormations(
            String titre, Boolean planifiee,
            Integer minDuree, Integer maxDuree,
            String sortBy, String direction);

    // Statistiques
    Map<String, Long> getDemandesCountByFormationTitle();
    long countActiveFormations();

    // Nouvelles méthodes pour les statistiques
    Map<String, Map<String, Long>> getStatutsCountByFormationTitle();
    Map<String, Long> analyzeTitleKeywords();
    Map<String, Double> getSuccessRateByFormationTitle();
    Map<String, Object> analyzeTitleLengthImpact();
    double getAverageDemandsPerFormation();

    FormationDTO getFormationWithDemandCount(Long id);
    List<FormationDTO> getFormationsWithDemandCount();
    long countAllFormations();
}