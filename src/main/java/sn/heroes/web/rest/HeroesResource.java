package sn.heroes.web.rest;

import com.codahale.metrics.annotation.Timed;
import sn.heroes.domain.Heroes;
import sn.heroes.repository.HeroesRepository;
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
 * REST controller for managing Heroes.
 */
@RestController
@RequestMapping("/api")
public class HeroesResource {

    private final Logger log = LoggerFactory.getLogger(HeroesResource.class);

    private static final String ENTITY_NAME = "sunubHeroes";

    private final HeroesRepository heroesRepository;

    public HeroesResource(HeroesRepository heroesRepository) {
        this.heroesRepository = heroesRepository;
    }

    /**
     * POST  /heroes : Create a new heroes.
     *
     * @param heroes the heroes to create
     * @return the ResponseEntity with status 201 (Created) and with body the new heroes, or with status 400 (Bad Request) if the heroes has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/heroes")
    @Timed
    public ResponseEntity<Heroes> createHeroes(@RequestBody Heroes heroes) throws URISyntaxException {
        log.debug("REST request to save Heroes : {}", heroes);
        if (heroes.getId() != null) {
            throw new BadRequestAlertException("A new heroes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Heroes result = heroesRepository.save(heroes);
        return ResponseEntity.created(new URI("/api/heroes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /heroes : Updates an existing heroes.
     *
     * @param heroes the heroes to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated heroes,
     * or with status 400 (Bad Request) if the heroes is not valid,
     * or with status 500 (Internal Server Error) if the heroes couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/heroes")
    @Timed
    public ResponseEntity<Heroes> updateHeroes(@RequestBody Heroes heroes) throws URISyntaxException {
        log.debug("REST request to update Heroes : {}", heroes);
        if (heroes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Heroes result = heroesRepository.save(heroes);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, heroes.getId().toString()))
            .body(result);
    }

    /**
     * GET  /heroes : get all the heroes.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of heroes in body
     */
    @GetMapping("/heroes")
    @Timed
    public List<Heroes> getAllHeroes() {
        log.debug("REST request to get all Heroes");
        return heroesRepository.findAll();
    }

    /**
     * GET  /heroes/:id : get the "id" heroes.
     *
     * @param id the id of the heroes to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the heroes, or with status 404 (Not Found)
     */
    @GetMapping("/heroes/{id}")
    @Timed
    public ResponseEntity<Heroes> getHeroes(@PathVariable Long id) {
        log.debug("REST request to get Heroes : {}", id);
        Optional<Heroes> heroes = heroesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(heroes);
    }

    /**
     * DELETE  /heroes/:id : delete the "id" heroes.
     *
     * @param id the id of the heroes to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/heroes/{id}")
    @Timed
    public ResponseEntity<Void> deleteHeroes(@PathVariable Long id) {
        log.debug("REST request to delete Heroes : {}", id);

        heroesRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
