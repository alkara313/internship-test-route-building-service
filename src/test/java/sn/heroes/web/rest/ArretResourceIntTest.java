package sn.heroes.web.rest;

import sn.heroes.SunubApp;

import sn.heroes.domain.Arret;
import sn.heroes.repository.ArretRepository;
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
 * Test class for the ArretResource REST controller.
 *
 * @see ArretResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SunubApp.class)
public class ArretResourceIntTest {

    private static final String DEFAULT_ID_A = "AAAAAAAAAA";
    private static final String UPDATED_ID_A = "BBBBBBBBBB";

    private static final String DEFAULT_NOM_A = "AAAAAAAAAA";
    private static final String UPDATED_NOM_A = "BBBBBBBBBB";

    private static final String DEFAULT_LATTITUDE = "AAAAAAAAAA";
    private static final String UPDATED_LATTITUDE = "BBBBBBBBBB";

    private static final String DEFAULT_LONGITUDE = "AAAAAAAAAA";
    private static final String UPDATED_LONGITUDE = "BBBBBBBBBB";

    @Autowired
    private ArretRepository arretRepository;

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

    private MockMvc restArretMockMvc;

    private Arret arret;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ArretResource arretResource = new ArretResource(arretRepository);
        this.restArretMockMvc = MockMvcBuilders.standaloneSetup(arretResource)
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
    public static Arret createEntity(EntityManager em) {
        Arret arret = new Arret()
            .idA(DEFAULT_ID_A)
            .nomA(DEFAULT_NOM_A)
            .lattitude(DEFAULT_LATTITUDE)
            .longitude(DEFAULT_LONGITUDE);
        return arret;
    }

    @Before
    public void initTest() {
        arret = createEntity(em);
    }

    @Test
    @Transactional
    public void createArret() throws Exception {
        int databaseSizeBeforeCreate = arretRepository.findAll().size();

        // Create the Arret
        restArretMockMvc.perform(post("/api/arrets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(arret)))
            .andExpect(status().isCreated());

        // Validate the Arret in the database
        List<Arret> arretList = arretRepository.findAll();
        assertThat(arretList).hasSize(databaseSizeBeforeCreate + 1);
        Arret testArret = arretList.get(arretList.size() - 1);
        assertThat(testArret.getIdA()).isEqualTo(DEFAULT_ID_A);
        assertThat(testArret.getNomA()).isEqualTo(DEFAULT_NOM_A);
        assertThat(testArret.getLattitude()).isEqualTo(DEFAULT_LATTITUDE);
        assertThat(testArret.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    public void createArretWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = arretRepository.findAll().size();

        // Create the Arret with an existing ID
        arret.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restArretMockMvc.perform(post("/api/arrets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(arret)))
            .andExpect(status().isBadRequest());

        // Validate the Arret in the database
        List<Arret> arretList = arretRepository.findAll();
        assertThat(arretList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllArrets() throws Exception {
        // Initialize the database
        arretRepository.saveAndFlush(arret);

        // Get all the arretList
        restArretMockMvc.perform(get("/api/arrets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(arret.getId().intValue())))
            .andExpect(jsonPath("$.[*].idA").value(hasItem(DEFAULT_ID_A.toString())))
            .andExpect(jsonPath("$.[*].nomA").value(hasItem(DEFAULT_NOM_A.toString())))
            .andExpect(jsonPath("$.[*].lattitude").value(hasItem(DEFAULT_LATTITUDE.toString())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.toString())));
    }
    
    @Test
    @Transactional
    public void getArret() throws Exception {
        // Initialize the database
        arretRepository.saveAndFlush(arret);

        // Get the arret
        restArretMockMvc.perform(get("/api/arrets/{id}", arret.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(arret.getId().intValue()))
            .andExpect(jsonPath("$.idA").value(DEFAULT_ID_A.toString()))
            .andExpect(jsonPath("$.nomA").value(DEFAULT_NOM_A.toString()))
            .andExpect(jsonPath("$.lattitude").value(DEFAULT_LATTITUDE.toString()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingArret() throws Exception {
        // Get the arret
        restArretMockMvc.perform(get("/api/arrets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateArret() throws Exception {
        // Initialize the database
        arretRepository.saveAndFlush(arret);

        int databaseSizeBeforeUpdate = arretRepository.findAll().size();

        // Update the arret
        Arret updatedArret = arretRepository.findById(arret.getId()).get();
        // Disconnect from session so that the updates on updatedArret are not directly saved in db
        em.detach(updatedArret);
        updatedArret
            .idA(UPDATED_ID_A)
            .nomA(UPDATED_NOM_A)
            .lattitude(UPDATED_LATTITUDE)
            .longitude(UPDATED_LONGITUDE);

        restArretMockMvc.perform(put("/api/arrets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedArret)))
            .andExpect(status().isOk());

        // Validate the Arret in the database
        List<Arret> arretList = arretRepository.findAll();
        assertThat(arretList).hasSize(databaseSizeBeforeUpdate);
        Arret testArret = arretList.get(arretList.size() - 1);
        assertThat(testArret.getIdA()).isEqualTo(UPDATED_ID_A);
        assertThat(testArret.getNomA()).isEqualTo(UPDATED_NOM_A);
        assertThat(testArret.getLattitude()).isEqualTo(UPDATED_LATTITUDE);
        assertThat(testArret.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    public void updateNonExistingArret() throws Exception {
        int databaseSizeBeforeUpdate = arretRepository.findAll().size();

        // Create the Arret

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArretMockMvc.perform(put("/api/arrets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(arret)))
            .andExpect(status().isBadRequest());

        // Validate the Arret in the database
        List<Arret> arretList = arretRepository.findAll();
        assertThat(arretList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteArret() throws Exception {
        // Initialize the database
        arretRepository.saveAndFlush(arret);

        int databaseSizeBeforeDelete = arretRepository.findAll().size();

        // Get the arret
        restArretMockMvc.perform(delete("/api/arrets/{id}", arret.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Arret> arretList = arretRepository.findAll();
        assertThat(arretList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Arret.class);
        Arret arret1 = new Arret();
        arret1.setId(1L);
        Arret arret2 = new Arret();
        arret2.setId(arret1.getId());
        assertThat(arret1).isEqualTo(arret2);
        arret2.setId(2L);
        assertThat(arret1).isNotEqualTo(arret2);
        arret1.setId(null);
        assertThat(arret1).isNotEqualTo(arret2);
    }
}
