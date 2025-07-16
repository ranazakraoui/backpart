package com.example.demo.dto;

import lombok.Getter;

@Getter
public class FormationWithDemandCountDTO {
    // Getters
    private final Long formationId;
    private final String titre;
    private final Long demandCount;  // Renommé pour plus de clarté

    public FormationWithDemandCountDTO(Long formationId, String titre, Long demandCount) {
        this.formationId = formationId;
        this.titre = titre;
        this.demandCount = demandCount;
    }

}