package com.example.demo.service.mapper;

import com.example.demo.dto.FormationDTO;
import com.example.demo.model.Formation;

public class FormationMapper {

    // Convertit une entité Formation en DTO FormationDTO
    public static FormationDTO toDTO(Formation entity) {
        if (entity == null) {
            return null;
        }

        FormationDTO dto = new FormationDTO();
        dto.setId(entity.getId());
        dto.setTitre(entity.getTitre());
        dto.setDescription(entity.getDescription());
        dto.setDuree(entity.getDuree());
        dto.setPlanifiee(entity.isPlanifiee());

        return dto;
    }

    // Convertit un DTO FormationDTO en entité Formation
    public static Formation toEntity(FormationDTO dto) {
        if (dto == null) {
            return null;
        }

        Formation entity = new Formation();
        entity.setId(dto.getId()); // peut être null pour une nouvelle formation
        entity.setTitre(dto.getTitre());
        entity.setDescription(dto.getDescription());
        entity.setDuree(dto.getDuree());
        entity.setPlanifiee(dto.isPlanifiee());

        return entity;
    }
}
