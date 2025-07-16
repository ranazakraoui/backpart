package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Setter
@Getter
public class DemandeFormationDTO {
    private Long id;
    private Date dateDemande;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String emailCollaborateur;

    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nomCollaborateur;

    private String statut;

    @NotNull(message = "Veuillez sélectionner une formation")
    private Long formationId;

    private String formationTitre;
    private FormationDTO formation;

    // Ajout des méthodes de formatage
    public String getFormattedDateDemande() {
        if (this.dateDemande == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(this.dateDemande);
    }

    public String getFormattedDateDemande(String pattern) {
        if (this.dateDemande == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(this.dateDemande);
    }
}