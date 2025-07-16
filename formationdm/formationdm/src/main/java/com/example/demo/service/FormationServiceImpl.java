package com.example.demo.service;

import com.example.demo.dto.FormationDTO;
import com.example.demo.model.Formation;
import com.example.demo.repository.FormationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FormationServiceImpl implements FormationService {

    private final FormationRepository formationRepository;

    public FormationServiceImpl(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
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

    // Autres méthodes inchangées...
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




    // Méthodes helper
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
}