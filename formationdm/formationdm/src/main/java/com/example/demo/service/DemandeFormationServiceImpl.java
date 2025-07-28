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

import java.text.DateFormatSymbols;
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
    public DemandeFormationDTO createDemande(DemandeFormationDTO demandeDTO) {
        if (demandeDTO.getFormationId() == null) {
            throw new IllegalArgumentException("L'ID de formation est obligatoire");
        }

        demandeDTO.setStatut("EN_ATTENTE");
        demandeDTO.setDateDemande(new Date());

        return saveDemande(demandeDTO);
    }

    @Override
    @Deprecated
    public DemandeFormationDTO saveDemande(DemandeFormationDTO demandeDTO) {
        Formation formation = formationRepository.findById(demandeDTO.getFormationId())
                .orElseThrow(() -> new NoSuchElementException("Formation non trouvée avec l'ID: " + demandeDTO.getFormationId()));

        DemandeFormation demande = new DemandeFormation();
        demande.setDateDemande(demandeDTO.getDateDemande());
        demande.setEmailCollaborateur(demandeDTO.getEmailCollaborateur());
        demande.setNomCollaborateur(demandeDTO.getNomCollaborateur());
        demande.setStatut(demandeDTO.getStatut());
        demande.setFormation(formation);

        DemandeFormation savedDemande = demandeFormationRepository.save(demande);
        return convertToDto(savedDemande);
    }

    @Override
    public DemandeFormationDTO updateDemande(Long id, DemandeFormationDTO demandeDTO) {
        DemandeFormation demande = demandeFormationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Demande non trouvée avec l'ID: " + id));

        Formation formation = formationRepository.findById(demandeDTO.getFormationId())
                .orElseThrow(() -> new NoSuchElementException("Formation non trouvée avec l'ID: " + demandeDTO.getFormationId()));

        demande.setEmailCollaborateur(demandeDTO.getEmailCollaborateur());
        demande.setNomCollaborateur(demandeDTO.getNomCollaborateur());
        demande.setStatut(demandeDTO.getStatut());
        demande.setFormation(formation);

        if ("APPROUVEE".equals(demandeDTO.getStatut())) {
            long approvedCount = demandeFormationRepository.countByFormationIdAndStatut(
                    formation.getId(),
                    "APPROUVEE"
            );

            if (approvedCount >= 5) {
                formation.setPlanifiee(true);
                formationRepository.save(formation);
            }
        }

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

    @Override
    public List<DemandeFormationDTO> getAllDemandes() {
        return demandeFormationRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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
        return demandeFormationRepository.findTopFormationsWithDemandCount()
                .stream()
                .map(result -> new FormationWithDemandCountDTO(
                        (Long) result[0],
                        (String) result[1],
                        (Long) result[2]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public long countDistinctUsers() {
        return demandeFormationRepository.countDistinctEmailCollaborateur();
    }

    @Override
    public long countAllDemandes() {
        return demandeFormationRepository.count();
    }

    @Override
    public long countByStatut(String statut) {
        return demandeFormationRepository.countByStatut(statut);
    }

    @Override
    public Map<String, Long> getMonthlyEvolution() {
        List<Object[]> results = demandeFormationRepository.getMonthlyEvolution();
        Map<String, Long> monthlyStats = new LinkedHashMap<>();

        for (Object[] result : results) {
            int month = (int) result[0];
            long count = (long) result[1];
            monthlyStats.put(getMonthName(month), count);
        }

        // Remplir les mois manquants avec 0
        for (int i = 1; i <= 12; i++) {
            String monthName = getMonthName(i);
            monthlyStats.putIfAbsent(monthName, 0L);
        }

        return monthlyStats;
    }
    private String getMonthName(int month) {
        return new DateFormatSymbols(Locale.FRENCH).getMonths()[month-1];
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
}