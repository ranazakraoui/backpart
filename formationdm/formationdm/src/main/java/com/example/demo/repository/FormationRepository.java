package com.example.demo.repository;

import com.example.demo.model.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {

    List<Formation> findByPlanifiee(boolean planifiee);

    long countByPlanifiee(boolean planifiee);

    List<Formation> findByTitreContainingIgnoreCase(String titre);

    List<Formation> findByDureeBetween(int minDuree, int maxDuree);

    @Query("SELECT COUNT(d) FROM DemandeFormation d WHERE d.formation.id = :formationId")
    Long countDemandesByFormationId(@Param("formationId") Long formationId);
    @Query("SELECT f.titre, COUNT(d) FROM DemandeFormation d JOIN d.formation f GROUP BY f.titre")
    List<Object[]> countDemandesByFormationTitle();
    // Pour la pagination
    org.springframework.data.domain.Page<Formation> findByTitreContainingIgnoreCase(String titre, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<Formation> findByPlanifiee(boolean planifiee, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<Formation> findByDureeBetween(int minDuree, int maxDuree, org.springframework.data.domain.Pageable pageable);

    // Pour le tri
    List<Formation> findByTitreContainingIgnoreCase(String titre, org.springframework.data.domain.Sort sort);

    List<Formation> findByPlanifiee(boolean planifiee, org.springframework.data.domain.Sort sort);

    List<Formation> findByDureeBetween(int minDuree, int maxDuree, org.springframework.data.domain.Sort sort);
}