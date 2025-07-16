package com.example.demo.dto;

import jdk.jshell.Snippet;

public class FormationDTO {
    private Long id;
    private String titre;
    private String description;
    private int duree;
    private boolean planifiee;
    private Long nbDemandes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public boolean isPlanifiee() {
        return planifiee;
    }

    public void setPlanifiee(boolean planifiee) {
        this.planifiee = planifiee;
    }

    public Long getNbDemandes() {
        return nbDemandes;
    }

    public void setNbDemandes(Long nbDemandes) {
        this.nbDemandes = nbDemandes;
    }
}
