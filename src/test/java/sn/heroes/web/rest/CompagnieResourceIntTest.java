package sn.heroes.web.rest;

import sn.heroes.SunubApp;

import sn.heroes.domain.Compagnie;
import sn.heroes.repository.CompagnieRepository;
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
 * Test class for the CompagnieResource REST controller.
 *
 * @see CompagnieResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SunubApp.class)
public class CompagnieResourceIntTest {

    private static final String DEFAULT_ID_C = "AAAAAAAAAA";
    private static final String UPDATED_ID_C = "BBBBBBBBBB";

    private static final String DEFAULT_NOM_C = "AAAAAAAAAA";
    private static final String UPDATED_NOM_C = "BBBBBBBBBB";

    @Autowired
    private CompagnieRepository compagnieRepository;

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

    private MockMvc restCompagnieMockMvc;

    private Compagnie compagnie;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CompagnieResource compagnieResource = new CompagnieResource(compagnieRepository);
        this.restCompagnieMockMvc = MockMvcBuilders.standaloneSetup(compagnieResource)
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
    public static Compagnie createEntity(EntityManager em) {
        Compagnie compagnie = new Compagnie()
            .idC(DEFAULT_ID_C)
            .nomC(DEFAULT_NOM_C);
        return compagnie;
    }

    @Before
    public void initTest() {
        compagnie = createEntity(em);
    }

    @Test
    @Transactional
    public void createCompagnie() throws Exception {
        int databaseSizeBeforeCreate = compagnieRepository.findAll().size();

        // Create the Compagnie
        restCompagnieMockMvc.perform(post("/api/compagnies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(compagnie)))
            .andExpect(status().isCreated());

        // Validate the Compagnie in the database
        List<Compagnie> compagnieList = compagnieRepository.findAll();
        assertThat(compagnieList).hasSize(databaseSizeBeforeCreate + 1);
        Compagnie testCompagnie = compagnieList.get(compagnieList.size() - 1);
        assertThat(testCompagnie.getIdC()).isEqualTo(DEFAULT_ID_C);
        assertThat(testCompagnie.getNomC()).isEqualTo(DEFAULT_NOM_C);
    }

    @Test
    @Transactional
    public void createCompagnieWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = compagnieRepository.findAll().size();

        // Create the Compagnie with an existing ID
        compagnie.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompagnieMockMvc.perform(post("/api/compagnies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(compagnie)))
            .andExpect(status().isBadRequest());

        // Validate the Compagnie in the database
        List<Compagnie> compagnieList = compagnieRepository.findAll();
        assertThat(compagnieList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCompagnies() throws Exception {
        // Initialize the database
        compagnieRepository.saveAndFlush(compagnie);

        // Get all the compagnieList
        restCompagnieMockMvc.perform(get("/api/compagnies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compagnie.getId().intValue())))
            .andExpect(jsonPath("$.[*].idC").value(hasItem(DEFAULT_ID_C.toString())))
            .andExpect(jsonPath("$.[*].nomC").value(hasItem(DEFAULT_NOM_C.toString())));
    }
    
    @Test
    @Transactional
    public void getCompagnie() throws Exception {
        // Initialize the database
        compagnieRepository.saveAndFlush(compagnie);

        // Get the compagnie
        restCompagnieMockMvc.perform(get("/api/compagnies/{id}", compagnie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(compagnie.getId().intValue()))
            .andExpect(jsonPath("$.idC").value(DEFAULT_ID_C.toString()))
            .andExpect(jsonPath("$.nomC").value(DEFAULT_NOM_C.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCompagnie() throws Exception {
        // Get the compagnie
        restCompagnieMockMvc.perform(get("/api/compagnies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCompagnie() throws Exception {
        // Initialize the database
        compagnieRepository.saveAndFlush(compagnie);

        int databaseSizeBeforeUpdate = compagnieRepository.findAll().size();

        // Update the compagnie
        Compagnie updatedCompagnie = compagnieRepository.findById(compagnie.getId()).get();
        // Disconnect from session so that the updates on updatedCompagnie are not directly saved in db
        em.detach(updatedCompagnie);
        updatedCompagnie
            .idC(UPDATED_ID_C)
            .nomC(UPDATED_NOM_C);

        restCompagnieMockMvc.perform(put("/api/compagnies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCompagnie)))
            .andExpect(status().isOk());

        // Validate the Compagnie in the database
        List<Compagnie> compagnieList = compagnieRepository.findAll();
        assertThat(compagnieList).hasSize(databaseSizeBeforeUpdate);
        Compagnie testCompagnie = compagnieList.get(compagnieList.size() - 1);
        assertThat(testCompagnie.getIdC()).isEqualTo(UPDATED_ID_C);
        assertThat(testCompagnie.getNomC()).isEqualTo(UPDATED_NOM_C);
    }

    @Test
    @Transactional
    public void updateNonExistingCompagnie() throws Exception {
        int databaseSizeBeforeUpdate = compagnieRepository.findAll().size();

        // Create the Compagnie

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompagnieMockMvc.perform(put("/api/compagnies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(compagnie)))
            .andExpect(status().isBadRequest());

        // Validate the Compagnie in the database
        List<Compagnie> compagnieList = compagnieRepository.findAll();
        assertThat(compagnieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCompagnie() throws Exception {
        // Initialize the database
        compagnieRepository.saveAndFlush(compagnie);

        int databaseSizeBeforeDelete = compagnieRepository.findAll().size();

        // Get the compagnie
        restCompagnieMockMvc.perform(delete("/api/compagnies/{id}", compagnie.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Compagnie> compagnieList = compagnieRepository.findAll();
        assertThat(compagnieList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Compagnie.class);
        Compagnie compagnie1 = new Compagnie();
        compagnie1.setId(1L);
        Compagnie compagnie2 = new Compagnie();
        compagnie2.setId(compagnie1.getId());
        assertThat(compagnie1).isEqualTo(compagnie2);
        compagnie2.setId(2L);
        assertThat(compagnie1).isNotEqualTo(compagnie2);
        compagnie1.setId(null);
        assertThat(compagnie1).isNotEqualTo(compagnie2);
    }
}
