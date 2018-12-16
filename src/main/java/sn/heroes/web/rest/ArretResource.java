package sn.heroes.web.rest;

import com.codahale.metrics.annotation.Timed;
import sn.heroes.domain.Arret;
import sn.heroes.repository.ArretRepository;
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
 * REST controller for managing Arret.
 */
@RestController
@RequestMapping("/api")
public class ArretResource {

    private final Logger log = LoggerFactory.getLogger(ArretResource.class);

    private static final String ENTITY_NAME = "sunubArret";

    private final ArretRepository arretRepository;

    public ArretResource(ArretRepository arretRepository) {
        this.arretRepository = arretRepository;
    }

    /**
     * POST  /arrets : Create a new arret.
     *
     * @param arret the arret to create
     * @return the ResponseEntity with status 201 (Created) and with body the new arret, or with status 400 (Bad Request) if the arret has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/arrets")
    @Timed
    public ResponseEntity<Arret> createArret(@RequestBody Arret arret) throws URISyntaxException {
        log.debug("REST request to save Arret : {}", arret);
        if (arret.getId() != null) {
            throw new BadRequestAlertException("A new arret cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Arret result = arretRepository.save(arret);
        return ResponseEntity.created(new URI("/api/arrets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /arrets : Updates an existing arret.
     *
     * @param arret the arret to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated arret,
     * or with status 400 (Bad Request) if the arret is not valid,
     * or with status 500 (Internal Server Error) if the arret couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/arrets")
    @Timed
    public ResponseEntity<Arret> updateArret(@RequestBody Arret arret) throws URISyntaxException {
        log.debug("REST request to update Arret : {}", arret);
        if (arret.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Arret result = arretRepository.save(arret);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, arret.getId().toString()))
            .body(result);
    }

    /**
     * GET  /arrets : get all the arrets.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of arrets in body
     */
    @GetMapping("/arrets")
    @Timed
    public List<Arret> getAllArrets() {
        log.debug("REST request to get all Arrets");
        return arretRepository.findAll();
    }

    /**
     * GET  /arrets/:id : get the "id" arret.
     *
     * @param id the id of the arret to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the arret, or with status 404 (Not Found)
     */
    @GetMapping("/arrets/{id}")
    @Timed
    public ResponseEntity<Arret> getArret(@PathVariable Long id) {
        log.debug("REST request to get Arret : {}", id);
        Optional<Arret> arret = arretRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(arret);
    }

    /**
     * DELETE  /arrets/:id : delete the "id" arret.
     *
     * @param id the id of the arret to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/arrets/{id}")
    @Timed
    public ResponseEntity<Void> deleteArret(@PathVariable Long id) {
        log.debug("REST request to delete Arret : {}", id);

        arretRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
