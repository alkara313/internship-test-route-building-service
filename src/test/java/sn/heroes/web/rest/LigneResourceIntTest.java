package sn.heroes.web.rest;

import sn.heroes.SunubApp;

import sn.heroes.domain.Ligne;
import sn.heroes.repository.LigneRepository;
import sn.heroes.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;


import static sn.heroes.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LigneResource REST controller.
 *
 * @see LigneResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SunubApp.class)
public class LigneResourceIntTest {

    private static final String DEFAULT_ID_L = "AAAAAAAAAA";
    private static final String UPDATED_ID_L = "BBBBBBBBBB";

    private static final String DEFAULT_NUM_L = "AAAAAAAAAA";
    private static final String UPDATED_NUM_L = "BBBBBBBBBB";

    private static final String DEFAULT_NOM_L = "AAAAAAAAAA";
    private static final String UPDATED_NOM_L = "BBBBBBBBBB";

    private static final String DEFAULT_NOM_TRAJET = "AAAAAAAAAA";
    private static final String UPDATED_NOM_TRAJET = "BBBBBBBBBB";

    @Autowired
    private LigneRepository ligneRepository;

    @Mock
    private LigneRepository ligneRepositoryMock;

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

    private MockMvc restLigneMockMvc;

    private Ligne ligne;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LigneResource ligneResource = new LigneResource(ligneRepository);
        this.restLigneMockMvc = MockMvcBuilders.standaloneSetup(ligneResource)
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
    public static Ligne createEntity(EntityManager em) {
        Ligne ligne = new Ligne()
            .idL(DEFAULT_ID_L)
            .numL(DEFAULT_NUM_L)
            .nomL(DEFAULT_NOM_L)
            .nomTrajet(DEFAULT_NOM_TRAJET);
        return ligne;
    }

    @Before
    public void initTest() {
        ligne = createEntity(em);
    }

    @Test
    @Transactional
    public void createLigne() throws Exception {
        int databaseSizeBeforeCreate = ligneRepository.findAll().size();

        // Create the Ligne
        restLigneMockMvc.perform(post("/api/lignes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ligne)))
            .andExpect(status().isCreated());

        // Validate the Ligne in the database
        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeCreate + 1);
        Ligne testLigne = ligneList.get(ligneList.size() - 1);
        assertThat(testLigne.getIdL()).isEqualTo(DEFAULT_ID_L);
        assertThat(testLigne.getNumL()).isEqualTo(DEFAULT_NUM_L);
        assertThat(testLigne.getNomL()).isEqualTo(DEFAULT_NOM_L);
        assertThat(testLigne.getNomTrajet()).isEqualTo(DEFAULT_NOM_TRAJET);
    }

    @Test
    @Transactional
    public void createLigneWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ligneRepository.findAll().size();

        // Create the Ligne with an existing ID
        ligne.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLigneMockMvc.perform(post("/api/lignes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ligne)))
            .andExpect(status().isBadRequest());

        // Validate the Ligne in the database
        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllLignes() throws Exception {
        // Initialize the database
        ligneRepository.saveAndFlush(ligne);

        // Get all the ligneList
        restLigneMockMvc.perform(get("/api/lignes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligne.getId().intValue())))
            .andExpect(jsonPath("$.[*].idL").value(hasItem(DEFAULT_ID_L.toString())))
            .andExpect(jsonPath("$.[*].numL").value(hasItem(DEFAULT_NUM_L.toString())))
            .andExpect(jsonPath("$.[*].nomL").value(hasItem(DEFAULT_NOM_L.toString())))
            .andExpect(jsonPath("$.[*].nomTrajet").value(hasItem(DEFAULT_NOM_TRAJET.toString())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllLignesWithEagerRelationshipsIsEnabled() throws Exception {
        LigneResource ligneResource = new LigneResource(ligneRepositoryMock);
        when(ligneRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restLigneMockMvc = MockMvcBuilders.standaloneSetup(ligneResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restLigneMockMvc.perform(get("/api/lignes?eagerload=true"))
        .andExpect(status().isOk());

        verify(ligneRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllLignesWithEagerRelationshipsIsNotEnabled() throws Exception {
        LigneResource ligneResource = new LigneResource(ligneRepositoryMock);
            when(ligneRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restLigneMockMvc = MockMvcBuilders.standaloneSetup(ligneResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restLigneMockMvc.perform(get("/api/lignes?eagerload=true"))
        .andExpect(status().isOk());

            verify(ligneRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getLigne() throws Exception {
        // Initialize the database
        ligneRepository.saveAndFlush(ligne);

        // Get the ligne
        restLigneMockMvc.perform(get("/api/lignes/{id}", ligne.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(ligne.getId().intValue()))
            .andExpect(jsonPath("$.idL").value(DEFAULT_ID_L.toString()))
            .andExpect(jsonPath("$.numL").value(DEFAULT_NUM_L.toString()))
            .andExpect(jsonPath("$.nomL").value(DEFAULT_NOM_L.toString()))
            .andExpect(jsonPath("$.nomTrajet").value(DEFAULT_NOM_TRAJET.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLigne() throws Exception {
        // Get the ligne
        restLigneMockMvc.perform(get("/api/lignes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLigne() throws Exception {
        // Initialize the database
        ligneRepository.saveAndFlush(ligne);

        int databaseSizeBeforeUpdate = ligneRepository.findAll().size();

        // Update the ligne
        Ligne updatedLigne = ligneRepository.findById(ligne.getId()).get();
        // Disconnect from session so that the updates on updatedLigne are not directly saved in db
        em.detach(updatedLigne);
        updatedLigne
            .idL(UPDATED_ID_L)
            .numL(UPDATED_NUM_L)
            .nomL(UPDATED_NOM_L)
            .nomTrajet(UPDATED_NOM_TRAJET);

        restLigneMockMvc.perform(put("/api/lignes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLigne)))
            .andExpect(status().isOk());

        // Validate the Ligne in the database
        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeUpdate);
        Ligne testLigne = ligneList.get(ligneList.size() - 1);
        assertThat(testLigne.getIdL()).isEqualTo(UPDATED_ID_L);
        assertThat(testLigne.getNumL()).isEqualTo(UPDATED_NUM_L);
        assertThat(testLigne.getNomL()).isEqualTo(UPDATED_NOM_L);
        assertThat(testLigne.getNomTrajet()).isEqualTo(UPDATED_NOM_TRAJET);
    }

    @Test
    @Transactional
    public void updateNonExistingLigne() throws Exception {
        int databaseSizeBeforeUpdate = ligneRepository.findAll().size();

        // Create the Ligne

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneMockMvc.perform(put("/api/lignes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ligne)))
            .andExpect(status().isBadRequest());

        // Validate the Ligne in the database
        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLigne() throws Exception {
        // Initialize the database
        ligneRepository.saveAndFlush(ligne);

        int databaseSizeBeforeDelete = ligneRepository.findAll().size();

        // Get the ligne
        restLigneMockMvc.perform(delete("/api/lignes/{id}", ligne.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Ligne> ligneList = ligneRepository.findAll();
        assertThat(ligneList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ligne.class);
        Ligne ligne1 = new Ligne();
        ligne1.setId(1L);
        Ligne ligne2 = new Ligne();
        ligne2.setId(ligne1.getId());
        assertThat(ligne1).isEqualTo(ligne2);
        ligne2.setId(2L);
        assertThat(ligne1).isNotEqualTo(ligne2);
        ligne1.setId(null);
        assertThat(ligne1).isNotEqualTo(ligne2);
    }
}
