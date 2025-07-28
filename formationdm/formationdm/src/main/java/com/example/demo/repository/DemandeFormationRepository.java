package com.example.demo.repository;

import com.example.demo.model.DemandeFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeFormationRepository extends JpaRepository<DemandeFormation, Long> {

    long countByFormationId(Long formationId);

    long countByFormationIdAndStatut(Long formationId, String statut);

    @Query("SELECT d.formation.id, COUNT(d) FROM DemandeFormation d GROUP BY d.formation.id")
    List<Object[]> countAllByFormation();

    @Query("SELECT d.formation.id, d.formation.titre, COUNT(d) FROM DemandeFormation d GROUP BY d.formation.id, d.formation.titre ORDER BY COUNT(d) DESC")
    List<Object[]> findTopFormationsWithDemandCount();


    long countByStatut(String statut);

    @Query("SELECT MONTH(d.dateDemande), COUNT(d) FROM DemandeFormation d GROUP BY MONTH(d.dateDemande)")
    List<Object[]> getMonthlyEvolution();

    List<DemandeFormation> findByFormationId(Long formationId);

    @Query("SELECT f.titre, COUNT(d) FROM DemandeFormation d JOIN d.formation f GROUP BY f.titre")
    List<Object[]> countDemandesByFormationTitle();

    @Query("SELECT f.titre, d.statut, COUNT(d) FROM DemandeFormation d JOIN d.formation f GROUP BY f.titre, d.statut")
    List<Object[]> countStatutsByFormationTitle();

    @Query("SELECT f.titre, LENGTH(f.titre), COUNT(d) FROM DemandeFormation d JOIN d.formation f GROUP BY f.titre")
    List<Object[]> getTitleLengthStats();
    @Query("SELECT COUNT(DISTINCT d.emailCollaborateur) FROM DemandeFormation d")
    long countDistinctEmailCollaborateur();
    // Alias pour countDistinctUsers
    default long countDistinctUsers() {
        return countDistinctEmailCollaborateur(); // ← Cette ligne aussi modifiée
    }
}