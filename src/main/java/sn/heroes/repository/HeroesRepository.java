package sn.heroes.repository;

import sn.heroes.domain.Heroes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Heroes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HeroesRepository extends JpaRepository<Heroes, Long> {

}
