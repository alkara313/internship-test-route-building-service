package sn.heroes.web.rest;

import com.codahale.metrics.annotation.Timed;
import sn.heroes.domain.Ligne;
import sn.heroes.repository.LigneRepository;
import sn.heroes.web.rest.errors.BadRequestAlertException;
import sn.heroes.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Ligne.
 */
@RestController
@RequestMapping("/api")
public class LigneResource {

    private final Logger log = LoggerFactory.getLogger(LigneResource.class);

    private static final String ENTITY_NAME = "sunubLigne";

    private final LigneRepository ligneRepository;

    public LigneResource(LigneRepository ligneRepository) {
        this.ligneRepository = ligneRepository;
    }

    /**
     * POST  /lignes : Create a new ligne.
     *
     * @param ligne the ligne to create
     * @return the ResponseEntity with status 201 (Created) and with body the new ligne, or with status 400 (Bad Request) if the ligne has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/lignes")
    @Timed
    public ResponseEntity<Ligne> createLigne(@RequestBody Ligne ligne) throws URISyntaxException {
        log.debug("REST request to save Ligne : {}", ligne);
        if (ligne.getId() != null) {
            throw new BadRequestAlertException("A new ligne cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Ligne result = ligneRepository.save(ligne);
        return ResponseEntity.created(new URI("/api/lignes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /lignes : Updates an existing ligne.
     *
     * @param ligne the ligne to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated ligne,
     * or with status 400 (Bad Request) if the ligne is not valid,
     * or with status 500 (Internal Server Error) if the ligne couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/lignes")
    @Timed
    public ResponseEntity<Ligne> updateLigne(@RequestBody Ligne ligne) throws URISyntaxException {
        log.debug("REST request to update Ligne : {}", ligne);
        if (ligne.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Ligne result = ligneRepository.save(ligne);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, ligne.getId().toString()))
            .body(result);
    }

    /**
     * GET  /lignes : get all the lignes.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many)
     * @return the ResponseEntity with status 200 (OK) and the list of lignes in body
     */
    @GetMapping("/lignes")
    @Timed
    public List<Ligne> getAllLignes(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Lignes");
        return ligneRepository.findAllWithEagerRelationships();
    }

    /**
     * GET  /lignes/:id : get the "id" ligne.
     *
     * @param id the id of the ligne to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ligne, or with status 404 (Not Found)
     */
    @GetMapping("/lignes/{id}")
    @Timed
    public ResponseEntity<Ligne> getLigne(@PathVariable Long id) {
        log.debug("REST request to get Ligne : {}", id);
        Optional<Ligne> ligne = ligneRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(ligne);
    }

    /**
     * DELETE  /lignes/:id : delete the "id" ligne.
     *
     * @param id the id of the ligne to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/lignes/{id}")
    @Timed
    public ResponseEntity<Void> deleteLigne(@PathVariable Long id) {
        log.debug("REST request to delete Ligne : {}", id);

        ligneRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
