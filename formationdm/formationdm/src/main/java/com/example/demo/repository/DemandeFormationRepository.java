package com.example.demo.repository;

import com.example.demo.model.DemandeFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DemandeFormationRepository extends JpaRepository<DemandeFormation, Long> {
    long countByStatut(String statut);
    long countByFormationId(Long formationId);

    @Query("SELECT df.formation.id, COUNT(df) FROM DemandeFormation df GROUP BY df.formation.id")
    List<Object[]> countAllByFormation();

    @Query("SELECT f.id, f.titre, COUNT(df) FROM Formation f LEFT JOIN f.demandes df GROUP BY f.id, f.titre ORDER BY COUNT(df) DESC")
    List<Object[]> findTopFormationsWithDemandCount();
    long countByFormationIdAndStatut(Long formationId, String statut);
    List<DemandeFormation> findByFormationId(Long formationId);
}