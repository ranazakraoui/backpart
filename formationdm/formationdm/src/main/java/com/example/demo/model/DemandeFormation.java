package com.example.demo.model;

import jakarta.persistence.*;
import java.util.Date;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "demandeformation")
public class DemandeFormation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_demande", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDemande = new Date();

    @Column(name = "email_collaborateur", nullable = false)
    private String emailCollaborateur;

    @Column(name = "nom_collaborateur", nullable = false)
    private String nomCollaborateur;

    @Column(nullable = false)
    private String statut = "EN_ATTENTE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    public DemandeFormation() {}

    public String getFormattedDateDemande() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(this.dateDemande);
    }

    public String getFormattedDateDemande(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(this.dateDemande);
    }
    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(Date dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getEmailCollaborateur() {
        return emailCollaborateur;
    }

    public void setEmailCollaborateur(String emailCollaborateur) {
        this.emailCollaborateur = emailCollaborateur;
    }

    public String getNomCollaborateur() {
        return nomCollaborateur;
    }

    public void setNomCollaborateur(String nomCollaborateur) {
        this.nomCollaborateur = nomCollaborateur;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }
}