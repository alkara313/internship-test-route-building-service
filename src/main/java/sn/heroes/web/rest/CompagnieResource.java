package sn.heroes.web.rest;

import com.codahale.metrics.annotation.Timed;
import sn.heroes.domain.Compagnie;
import sn.heroes.repository.CompagnieRepository;
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
 * REST controller for managing Compagnie.
 */
@RestController
@RequestMapping("/api")
public class CompagnieResource {

    private final Logger log = LoggerFactory.getLogger(CompagnieResource.class);

    private static final String ENTITY_NAME = "sunubCompagnie";

    private final CompagnieRepository compagnieRepository;

    public CompagnieResource(CompagnieRepository compagnieRepository) {
        this.compagnieRepository = compagnieRepository;
    }

    /**
     * POST  /compagnies : Create a new compagnie.
     *
     * @param compagnie the compagnie to create
     * @return the ResponseEntity with status 201 (Created) and with body the new compagnie, or with status 400 (Bad Request) if the compagnie has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/compagnies")
    @Timed
    public ResponseEntity<Compagnie> createCompagnie(@RequestBody Compagnie compagnie) throws URISyntaxException {
        log.debug("REST request to save Compagnie : {}", compagnie);
        if (compagnie.getId() != null) {
            throw new BadRequestAlertException("A new compagnie cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Compagnie result = compagnieRepository.save(compagnie);
        return ResponseEntity.created(new URI("/api/compagnies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /compagnies : Updates an existing compagnie.
     *
     * @param compagnie the compagnie to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated compagnie,
     * or with status 400 (Bad Request) if the compagnie is not valid,
     * or with status 500 (Internal Server Error) if the compagnie couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/compagnies")
    @Timed
    public ResponseEntity<Compagnie> updateCompagnie(@RequestBody Compagnie compagnie) throws URISyntaxException {
        log.debug("REST request to update Compagnie : {}", compagnie);
        if (compagnie.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Compagnie result = compagnieRepository.save(compagnie);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, compagnie.getId().toString()))
            .body(result);
    }

    /**
     * GET  /compagnies : get all the compagnies.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of compagnies in body
     */
    @GetMapping("/compagnies")
    @Timed
    public List<Compagnie> getAllCompagnies() {
        log.debug("REST request to get all Compagnies");
        return compagnieRepository.findAll();
    }

    /**
     * GET  /compagnies/:id : get the "id" compagnie.
     *
     * @param id the id of the compagnie to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the compagnie, or with status 404 (Not Found)
     */
    @GetMapping("/compagnies/{id}")
    @Timed
    public ResponseEntity<Compagnie> getCompagnie(@PathVariable Long id) {
        log.debug("REST request to get Compagnie : {}", id);
        Optional<Compagnie> compagnie = compagnieRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(compagnie);
    }

    /**
     * DELETE  /compagnies/:id : delete the "id" compagnie.
     *
     * @param id the id of the compagnie to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/compagnies/{id}")
    @Timed
    public ResponseEntity<Void> deleteCompagnie(@PathVariable Long id) {
        log.debug("REST request to delete Compagnie : {}", id);

        compagnieRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
