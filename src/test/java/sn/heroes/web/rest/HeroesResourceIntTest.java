package sn.heroes.web.rest;

import sn.heroes.SunubApp;

import sn.heroes.domain.Heroes;
import sn.heroes.repository.HeroesRepository;
import sn.heroes.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;


import static sn.heroes.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the HeroesResource REST controller.
 *
 * @see HeroesResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SunubApp.class)
public class HeroesResourceIntTest {

    private static final String DEFAULT_ID_HEROES = "AAAAAAAAAA";
    private static final String UPDATED_ID_HEROES = "BBBBBBBBBB";

    private static final String DEFAULT_NOM_H = "AAAAAAAAAA";
    private static final String UPDATED_NOM_H = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM_H = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM_H = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    @Autowired
    private HeroesRepository heroesRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restHeroesMockMvc;

    private Heroes heroes;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final HeroesResource heroesResource = new HeroesResource(heroesRepository);
        this.restHeroesMockMvc = MockMvcBuilders.standaloneSetup(heroesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Heroes createEntity(EntityManager em) {
        Heroes heroes = new Heroes()
            .idHeroes(DEFAULT_ID_HEROES)
            .nomH(DEFAULT_NOM_H)
            .prenomH(DEFAULT_PRENOM_H)
            .adresse(DEFAULT_ADRESSE);
        return heroes;
    }

    @Before
    public void initTest() {
        heroes = createEntity(em);
    }

    @Test
    @Transactional
    public void createHeroes() throws Exception {
        int databaseSizeBeforeCreate = heroesRepository.findAll().size();

        // Create the Heroes
        restHeroesMockMvc.perform(post("/api/heroes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(heroes)))
            .andExpect(status().isCreated());

        // Validate the Heroes in the database
        List<Heroes> heroesList = heroesRepository.findAll();
        assertThat(heroesList).hasSize(databaseSizeBeforeCreate + 1);
        Heroes testHeroes = heroesList.get(heroesList.size() - 1);
        assertThat(testHeroes.getIdHeroes()).isEqualTo(DEFAULT_ID_HEROES);
        assertThat(testHeroes.getNomH()).isEqualTo(DEFAULT_NOM_H);
        assertThat(testHeroes.getPrenomH()).isEqualTo(DEFAULT_PRENOM_H);
        assertThat(testHeroes.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
    }

    @Test
    @Transactional
    public void createHeroesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = heroesRepository.findAll().size();

        // Create the Heroes with an existing ID
        heroes.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restHeroesMockMvc.perform(post("/api/heroes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(heroes)))
            .andExpect(status().isBadRequest());

        // Validate the Heroes in the database
        List<Heroes> heroesList = heroesRepository.findAll();
        assertThat(heroesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllHeroes() throws Exception {
        // Initialize the database
        heroesRepository.saveAndFlush(heroes);

        // Get all the heroesList
        restHeroesMockMvc.perform(get("/api/heroes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(heroes.getId().intValue())))
            .andExpect(jsonPath("$.[*].idHeroes").value(hasItem(DEFAULT_ID_HEROES.toString())))
            .andExpect(jsonPath("$.[*].nomH").value(hasItem(DEFAULT_NOM_H.toString())))
            .andExpect(jsonPath("$.[*].prenomH").value(hasItem(DEFAULT_PRENOM_H.toString())))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE.toString())));
    }
    
    @Test
    @Transactional
    public void getHeroes() throws Exception {
        // Initialize the database
        heroesRepository.saveAndFlush(heroes);

        // Get the heroes
        restHeroesMockMvc.perform(get("/api/heroes/{id}", heroes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(heroes.getId().intValue()))
            .andExpect(jsonPath("$.idHeroes").value(DEFAULT_ID_HEROES.toString()))
            .andExpect(jsonPath("$.nomH").value(DEFAULT_NOM_H.toString()))
            .andExpect(jsonPath("$.prenomH").value(DEFAULT_PRENOM_H.toString()))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingHeroes() throws Exception {
        // Get the heroes
        restHeroesMockMvc.perform(get("/api/heroes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateHeroes() throws Exception {
        // Initialize the database
        heroesRepository.saveAndFlush(heroes);

        int databaseSizeBeforeUpdate = heroesRepository.findAll().size();

        // Update the heroes
        Heroes updatedHeroes = heroesRepository.findById(heroes.getId()).get();
        // Disconnect from session so that the updates on updatedHeroes are not directly saved in db
        em.detach(updatedHeroes);
        updatedHeroes
            .idHeroes(UPDATED_ID_HEROES)
            .nomH(UPDATED_NOM_H)
            .prenomH(UPDATED_PRENOM_H)
            .adresse(UPDATED_ADRESSE);

        restHeroesMockMvc.perform(put("/api/heroes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedHeroes)))
            .andExpect(status().isOk());

        // Validate the Heroes in the database
        List<Heroes> heroesList = heroesRepository.findAll();
        assertThat(heroesList).hasSize(databaseSizeBeforeUpdate);
        Heroes testHeroes = heroesList.get(heroesList.size() - 1);
        assertThat(testHeroes.getIdHeroes()).isEqualTo(UPDATED_ID_HEROES);
        assertThat(testHeroes.getNomH()).isEqualTo(UPDATED_NOM_H);
        assertThat(testHeroes.getPrenomH()).isEqualTo(UPDATED_PRENOM_H);
        assertThat(testHeroes.getAdresse()).isEqualTo(UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    public void updateNonExistingHeroes() throws Exception {
        int databaseSizeBeforeUpdate = heroesRepository.findAll().size();

        // Create the Heroes

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHeroesMockMvc.perform(put("/api/heroes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(heroes)))
            .andExpect(status().isBadRequest());

        // Validate the Heroes in the database
        List<Heroes> heroesList = heroesRepository.findAll();
        assertThat(heroesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteHeroes() throws Exception {
        // Initialize the database
        heroesRepository.saveAndFlush(heroes);

        int databaseSizeBeforeDelete = heroesRepository.findAll().size();

        // Get the heroes
        restHeroesMockMvc.perform(delete("/api/heroes/{id}", heroes.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Heroes> heroesList = heroesRepository.findAll();
        assertThat(heroesList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Heroes.class);
        Heroes heroes1 = new Heroes();
        heroes1.setId(1L);
        Heroes heroes2 = new Heroes();
        heroes2.setId(heroes1.getId());
        assertThat(heroes1).isEqualTo(heroes2);
        heroes2.setId(2L);
        assertThat(heroes1).isNotEqualTo(heroes2);
        heroes1.setId(null);
        assertThat(heroes1).isNotEqualTo(heroes2);
    }
}
