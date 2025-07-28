package com.example.demo.service;

import com.example.demo.dto.FormationDTO;
import com.example.demo.model.Formation;
import com.example.demo.repository.DemandeFormationRepository;
import com.example.demo.repository.FormationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FormationServiceImpl implements FormationService {

    private final FormationRepository formationRepository;
    private final DemandeFormationRepository demandeFormationRepository;

    public FormationServiceImpl(FormationRepository formationRepository,
                                DemandeFormationRepository demandeFormationRepository) {
        this.formationRepository = formationRepository;
        this.demandeFormationRepository = demandeFormationRepository;
    }

    @Override
    public long countActiveFormations() {
        return formationRepository.countByPlanifiee(true);
    }

    @Override
    public List<FormationDTO> findAll() {
        return formationRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FormationDTO> getFilteredFormationsPaginated(
            int page, int size,
            String titre, Boolean planifiee,
            Integer minDuree, Integer maxDuree,
            String sortBy, String direction) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (titre != null && !titre.isEmpty()) {
            return formationRepository.findByTitreContainingIgnoreCase(titre, pageable)
                    .map(this::toDTO);
        } else if (planifiee != null) {
            return formationRepository.findByPlanifiee(planifiee, pageable)
                    .map(this::toDTO);
        } else if (minDuree != null && maxDuree != null) {
            return formationRepository.findByDureeBetween(minDuree, maxDuree, pageable)
                    .map(this::toDTO);
        }

        return formationRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public FormationDTO saveFormation(FormationDTO formationDTO) {
        Formation formation = toEntity(formationDTO);
        return toDTO(formationRepository.save(formation));
    }

    @Override
    public FormationDTO updateFormation(FormationDTO formationDTO) {
        if (formationDTO.getId() == null) {
            throw new IllegalArgumentException("ID de formation requis pour la mise à jour");
        }
        return saveFormation(formationDTO);
    }

    @Override
    public void deleteFormation(Long id) {
        formationRepository.deleteById(id);
    }

    @Override
    public FormationDTO getFormationById(Long id) {
        return formationRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée ID: " + id));
    }

    @Override
    public FormationDTO getFormationWithDemandCount(Long id) {
        FormationDTO dto = getFormationById(id);
        dto.setNbDemandes(formationRepository.countDemandesByFormationId(id));
        return dto;
    }

    @Override
    public Formation getFormationEntityById(Long id) {
        return formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée ID: " + id));
    }

    @Override
    public List<FormationDTO> getFormationsWithDemandCount() {
        return formationRepository.findAll().stream()
                .map(formation -> {
                    FormationDTO dto = toDTO(formation);
                    dto.setNbDemandes(formationRepository.countDemandesByFormationId(formation.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<FormationDTO> getAllFormationsPaginated(int page, int size) {
        return formationRepository.findAll(PageRequest.of(page, size))
                .map(this::toDTO);
    }

    @Override
    public long countAllFormations() {
        return formationRepository.count();
    }

    @Override
    public List<FormationDTO> getAllFormations() {
        return formationRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FormationDTO> getAllFormationsSorted(String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return formationRepository.findAll(sort).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FormationDTO> getFilteredFormations(
            String titre, Boolean planifiee,
            Integer minDuree, Integer maxDuree,
            String sortBy, String direction) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

        if (titre != null && !titre.isEmpty()) {
            return formationRepository.findByTitreContainingIgnoreCase(titre, sort)
                    .stream().map(this::toDTO).collect(Collectors.toList());
        } else if (planifiee != null) {
            return formationRepository.findByPlanifiee(planifiee, sort)
                    .stream().map(this::toDTO).collect(Collectors.toList());
        } else if (minDuree != null && maxDuree != null) {
            return formationRepository.findByDureeBetween(minDuree, maxDuree, sort)
                    .stream().map(this::toDTO).collect(Collectors.toList());
        }

        return formationRepository.findAll(sort)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean isFormationDisponible(Long formationId) {
        FormationDTO formation = getFormationById(formationId);
        return formation != null && formation.isPlanifiee();
    }

    @Override
    public Map<String, Long> getDemandesCountByFormationTitle() {
        List<Object[]> results = demandeFormationRepository.countDemandesByFormationTitle();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1],
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public Map<String, Map<String, Long>> getStatutsCountByFormationTitle() {
        List<Object[]> results = demandeFormationRepository.countStatutsByFormationTitle();
        return results.stream()
                .collect(Collectors.groupingBy(
                        result -> (String) result[0],
                        Collectors.toMap(
                                result -> (String) result[1],
                                result -> (Long) result[2]
                        )
                ));
    }

    @Override
    public Map<String, Long> analyzeTitleKeywords() {
        List<String> titles = formationRepository.findAll().stream()
                .map(Formation::getTitre)
                .collect(Collectors.toList());

        return titles.stream()
                .map(String::toLowerCase)
                .flatMap(title -> Arrays.stream(title.split("\\s+|[-]")))
                .filter(word -> word.length() > 3 && !isCommonWord(word))
                .collect(Collectors.groupingBy(
                        word -> word,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(15)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public Map<String, Double> getSuccessRateByFormationTitle() {
        Map<String, Map<String, Long>> stats = getStatutsCountByFormationTitle();

        return stats.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            long approved = entry.getValue().getOrDefault("APPROUVEE", 0L);
                            long total = entry.getValue().values().stream().mapToLong(Long::longValue).sum();
                            return total > 0 ? (approved * 100.0 / total) : 0.0;
                        }
                ));
    }

    @Override
    public Map<String, Object> analyzeTitleLengthImpact() {
        List<Object[]> results = demandeFormationRepository.getTitleLengthStats();

        Map<String, Object> analysis = new LinkedHashMap<>();
        analysis.put("avgLength", results.stream()
                .mapToInt(r -> ((Number) r[1]).intValue())
                .average()
                .orElse(0));

        analysis.put("lengthDemandCorrelation", calculateCorrelation(results));

        return analysis;
    }

    @Override
    public double getAverageDemandsPerFormation() {
        Long totalDemands = demandeFormationRepository.count();
        Long totalFormations = formationRepository.count();
        return totalFormations > 0 ? totalDemands.doubleValue() / totalFormations : 0;
    }

    private Formation toEntity(FormationDTO dto) {
        Formation entity = new Formation();
        entity.setId(dto.getId());
        entity.setTitre(dto.getTitre());
        entity.setDescription(dto.getDescription());
        entity.setDuree(dto.getDuree());
        entity.setPlanifiee(dto.isPlanifiee());
        return entity;
    }

    private FormationDTO toDTO(Formation entity) {
        FormationDTO dto = new FormationDTO();
        dto.setId(entity.getId());
        dto.setTitre(entity.getTitre());
        dto.setDescription(entity.getDescription());
        dto.setDuree(entity.getDuree());
        dto.setPlanifiee(entity.isPlanifiee());
        return dto;
    }

    private boolean isCommonWord(String word) {
        Set<String> commonWords = Set.of("formation", "pour", "avec", "dans", "aux");
        return commonWords.contains(word);
    }

    private double calculateCorrelation(List<Object[]> data) {
        if (data.isEmpty()) return 0.0;

        double[] lengths = data.stream().mapToDouble(r -> ((Number) r[1]).doubleValue()).toArray();
        double[] counts = data.stream().mapToDouble(r -> ((Number) r[2]).doubleValue()).toArray();

        double sumX = 0.0, sumY = 0.0, sumXY = 0.0;
        double sumX2 = 0.0, sumY2 = 0.0;
        int n = lengths.length;

        for (int i = 0; i < n; i++) {
            sumX += lengths[i];
            sumY += counts[i];
            sumXY += lengths[i] * counts[i];
            sumX2 += lengths[i] * lengths[i];
            sumY2 += counts[i] * counts[i];
        }

        double numerator = sumXY - (sumX * sumY / n);
        double denominator = Math.sqrt((sumX2 - (sumX * sumX / n)) * (sumY2 - (sumY * sumY / n)));

        return denominator != 0 ? numerator / denominator : 0.0;
    }
}