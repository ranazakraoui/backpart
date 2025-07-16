package com.example.demo.repository;

import com.example.demo.model.Formation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FormationRepository extends JpaRepository<Formation, Long> {

    // Méthodes de base
    Page<Formation> findAll(Pageable pageable);
    List<Formation> findAll(Sort sort);

    // Méthodes de filtrage avec pagination
    List<Formation> findByTitreContainingIgnoreCase(String titre, Sort sort);
    List<Formation> findByPlanifiee(boolean planifiee, Sort sort);
    List<Formation> findByDureeBetween(int minDuree, int maxDuree, Sort sort);

    // ➕ AJOUT À FAIRE ICI : version avec Pageable
    Page<Formation> findByTitreContainingIgnoreCase(String titre, Pageable pageable);
    Page<Formation> findByPlanifiee(Boolean planifiee, Pageable pageable);
    Page<Formation> findByDureeBetween(Integer minDuree, Integer maxDuree, Pageable pageable);

    // Méthodes de comptage
    @Query("SELECT COUNT(d) FROM DemandeFormation d WHERE d.formation.id = :formationId")
    Long countDemandesByFormationId(@Param("formationId") Long formationId);

    Long countByDemandes_Formation_Id(Long id);
}
