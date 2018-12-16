package sn.heroes.repository;

import sn.heroes.domain.Ligne;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Ligne entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LigneRepository extends JpaRepository<Ligne, Long> {

    @Query(value = "select distinct ligne from Ligne ligne left join fetch ligne.arrets",
        countQuery = "select count(distinct ligne) from Ligne ligne")
    Page<Ligne> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct ligne from Ligne ligne left join fetch ligne.arrets")
    List<Ligne> findAllWithEagerRelationships();

    @Query("select ligne from Ligne ligne left join fetch ligne.arrets where ligne.id =:id")
    Optional<Ligne> findOneWithEagerRelationships(@Param("id") Long id);

}
