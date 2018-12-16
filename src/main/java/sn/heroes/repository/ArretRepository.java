package sn.heroes.repository;

import sn.heroes.domain.Arret;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Arret entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArretRepository extends JpaRepository<Arret, Long> {

}
