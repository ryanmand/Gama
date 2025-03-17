package com.alunometa.web.rest;

import static com.alunometa.domain.AlunoAsserts.*;
import static com.alunometa.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alunometa.IntegrationTest;
import com.alunometa.domain.Aluno;
import com.alunometa.repository.AlunoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AlunoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AlunoResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_NASCIMENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_NASCIMENTO = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATA_NASCIMENTO = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_TELEFONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/alunos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAlunoMockMvc;

    private Aluno aluno;

    private Aluno insertedAluno;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Aluno createEntity() {
        return new Aluno().nome(DEFAULT_NOME).email(DEFAULT_EMAIL).dataNascimento(DEFAULT_DATA_NASCIMENTO).telefone(DEFAULT_TELEFONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Aluno createUpdatedEntity() {
        return new Aluno().nome(UPDATED_NOME).email(UPDATED_EMAIL).dataNascimento(UPDATED_DATA_NASCIMENTO).telefone(UPDATED_TELEFONE);
    }

    @BeforeEach
    public void initTest() {
        aluno = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAluno != null) {
            alunoRepository.delete(insertedAluno);
            insertedAluno = null;
        }
    }

    @Test
    @Transactional
    void createAluno() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Aluno
        var returnedAluno = om.readValue(
            restAlunoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aluno)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Aluno.class
        );

        // Validate the Aluno in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAlunoUpdatableFieldsEquals(returnedAluno, getPersistedAluno(returnedAluno));

        insertedAluno = returnedAluno;
    }

    @Test
    @Transactional
    void createAlunoWithExistingId() throws Exception {
        // Create the Aluno with an existing ID
        aluno.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlunoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aluno)))
            .andExpect(status().isBadRequest());

        // Validate the Aluno in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aluno.setNome(null);

        // Create the Aluno, which fails.

        restAlunoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aluno)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aluno.setEmail(null);

        // Create the Aluno, which fails.

        restAlunoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aluno)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDataNascimentoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aluno.setDataNascimento(null);

        // Create the Aluno, which fails.

        restAlunoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aluno)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAlunos() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList
        restAlunoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aluno.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].dataNascimento").value(hasItem(DEFAULT_DATA_NASCIMENTO.toString())))
            .andExpect(jsonPath("$.[*].telefone").value(hasItem(DEFAULT_TELEFONE)));
    }

    @Test
    @Transactional
    void getAluno() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get the aluno
        restAlunoMockMvc
            .perform(get(ENTITY_API_URL_ID, aluno.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aluno.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.dataNascimento").value(DEFAULT_DATA_NASCIMENTO.toString()))
            .andExpect(jsonPath("$.telefone").value(DEFAULT_TELEFONE));
    }

    @Test
    @Transactional
    void getAlunosByIdFiltering() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        Long id = aluno.getId();

        defaultAlunoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAlunoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAlunoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAlunosByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where nome equals to
        defaultAlunoFiltering("nome.equals=" + DEFAULT_NOME, "nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllAlunosByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where nome in
        defaultAlunoFiltering("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME, "nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllAlunosByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where nome is not null
        defaultAlunoFiltering("nome.specified=true", "nome.specified=false");
    }

    @Test
    @Transactional
    void getAllAlunosByNomeContainsSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where nome contains
        defaultAlunoFiltering("nome.contains=" + DEFAULT_NOME, "nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllAlunosByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where nome does not contain
        defaultAlunoFiltering("nome.doesNotContain=" + UPDATED_NOME, "nome.doesNotContain=" + DEFAULT_NOME);
    }

    @Test
    @Transactional
    void getAllAlunosByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where email equals to
        defaultAlunoFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllAlunosByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where email in
        defaultAlunoFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllAlunosByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where email is not null
        defaultAlunoFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllAlunosByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where email contains
        defaultAlunoFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllAlunosByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where email does not contain
        defaultAlunoFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllAlunosByDataNascimentoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where dataNascimento equals to
        defaultAlunoFiltering("dataNascimento.equals=" + DEFAULT_DATA_NASCIMENTO, "dataNascimento.equals=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllAlunosByDataNascimentoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where dataNascimento in
        defaultAlunoFiltering(
            "dataNascimento.in=" + DEFAULT_DATA_NASCIMENTO + "," + UPDATED_DATA_NASCIMENTO,
            "dataNascimento.in=" + UPDATED_DATA_NASCIMENTO
        );
    }

    @Test
    @Transactional
    void getAllAlunosByDataNascimentoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where dataNascimento is not null
        defaultAlunoFiltering("dataNascimento.specified=true", "dataNascimento.specified=false");
    }

    @Test
    @Transactional
    void getAllAlunosByDataNascimentoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where dataNascimento is greater than or equal to
        defaultAlunoFiltering(
            "dataNascimento.greaterThanOrEqual=" + DEFAULT_DATA_NASCIMENTO,
            "dataNascimento.greaterThanOrEqual=" + UPDATED_DATA_NASCIMENTO
        );
    }

    @Test
    @Transactional
    void getAllAlunosByDataNascimentoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where dataNascimento is less than or equal to
        defaultAlunoFiltering(
            "dataNascimento.lessThanOrEqual=" + DEFAULT_DATA_NASCIMENTO,
            "dataNascimento.lessThanOrEqual=" + SMALLER_DATA_NASCIMENTO
        );
    }

    @Test
    @Transactional
    void getAllAlunosByDataNascimentoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where dataNascimento is less than
        defaultAlunoFiltering("dataNascimento.lessThan=" + UPDATED_DATA_NASCIMENTO, "dataNascimento.lessThan=" + DEFAULT_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllAlunosByDataNascimentoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where dataNascimento is greater than
        defaultAlunoFiltering(
            "dataNascimento.greaterThan=" + SMALLER_DATA_NASCIMENTO,
            "dataNascimento.greaterThan=" + DEFAULT_DATA_NASCIMENTO
        );
    }

    @Test
    @Transactional
    void getAllAlunosByTelefoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where telefone equals to
        defaultAlunoFiltering("telefone.equals=" + DEFAULT_TELEFONE, "telefone.equals=" + UPDATED_TELEFONE);
    }

    @Test
    @Transactional
    void getAllAlunosByTelefoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where telefone in
        defaultAlunoFiltering("telefone.in=" + DEFAULT_TELEFONE + "," + UPDATED_TELEFONE, "telefone.in=" + UPDATED_TELEFONE);
    }

    @Test
    @Transactional
    void getAllAlunosByTelefoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where telefone is not null
        defaultAlunoFiltering("telefone.specified=true", "telefone.specified=false");
    }

    @Test
    @Transactional
    void getAllAlunosByTelefoneContainsSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where telefone contains
        defaultAlunoFiltering("telefone.contains=" + DEFAULT_TELEFONE, "telefone.contains=" + UPDATED_TELEFONE);
    }

    @Test
    @Transactional
    void getAllAlunosByTelefoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        // Get all the alunoList where telefone does not contain
        defaultAlunoFiltering("telefone.doesNotContain=" + UPDATED_TELEFONE, "telefone.doesNotContain=" + DEFAULT_TELEFONE);
    }

    private void defaultAlunoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAlunoShouldBeFound(shouldBeFound);
        defaultAlunoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAlunoShouldBeFound(String filter) throws Exception {
        restAlunoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aluno.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].dataNascimento").value(hasItem(DEFAULT_DATA_NASCIMENTO.toString())))
            .andExpect(jsonPath("$.[*].telefone").value(hasItem(DEFAULT_TELEFONE)));

        // Check, that the count call also returns 1
        restAlunoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAlunoShouldNotBeFound(String filter) throws Exception {
        restAlunoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAlunoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAluno() throws Exception {
        // Get the aluno
        restAlunoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAluno() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aluno
        Aluno updatedAluno = alunoRepository.findById(aluno.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAluno are not directly saved in db
        em.detach(updatedAluno);
        updatedAluno.nome(UPDATED_NOME).email(UPDATED_EMAIL).dataNascimento(UPDATED_DATA_NASCIMENTO).telefone(UPDATED_TELEFONE);

        restAlunoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAluno.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAluno))
            )
            .andExpect(status().isOk());

        // Validate the Aluno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAlunoToMatchAllProperties(updatedAluno);
    }

    @Test
    @Transactional
    void putNonExistingAluno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aluno.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlunoMockMvc
            .perform(put(ENTITY_API_URL_ID, aluno.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aluno)))
            .andExpect(status().isBadRequest());

        // Validate the Aluno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAluno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aluno.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlunoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aluno))
            )
            .andExpect(status().isBadRequest());

        // Validate the Aluno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAluno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aluno.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlunoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aluno)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Aluno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAlunoWithPatch() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aluno using partial update
        Aluno partialUpdatedAluno = new Aluno();
        partialUpdatedAluno.setId(aluno.getId());

        partialUpdatedAluno.nome(UPDATED_NOME).email(UPDATED_EMAIL).telefone(UPDATED_TELEFONE);

        restAlunoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAluno.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAluno))
            )
            .andExpect(status().isOk());

        // Validate the Aluno in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlunoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAluno, aluno), getPersistedAluno(aluno));
    }

    @Test
    @Transactional
    void fullUpdateAlunoWithPatch() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aluno using partial update
        Aluno partialUpdatedAluno = new Aluno();
        partialUpdatedAluno.setId(aluno.getId());

        partialUpdatedAluno.nome(UPDATED_NOME).email(UPDATED_EMAIL).dataNascimento(UPDATED_DATA_NASCIMENTO).telefone(UPDATED_TELEFONE);

        restAlunoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAluno.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAluno))
            )
            .andExpect(status().isOk());

        // Validate the Aluno in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlunoUpdatableFieldsEquals(partialUpdatedAluno, getPersistedAluno(partialUpdatedAluno));
    }

    @Test
    @Transactional
    void patchNonExistingAluno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aluno.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlunoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aluno.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(aluno))
            )
            .andExpect(status().isBadRequest());

        // Validate the Aluno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAluno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aluno.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlunoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(aluno))
            )
            .andExpect(status().isBadRequest());

        // Validate the Aluno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAluno() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aluno.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlunoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(aluno)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Aluno in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAluno() throws Exception {
        // Initialize the database
        insertedAluno = alunoRepository.saveAndFlush(aluno);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the aluno
        restAlunoMockMvc
            .perform(delete(ENTITY_API_URL_ID, aluno.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return alunoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Aluno getPersistedAluno(Aluno aluno) {
        return alunoRepository.findById(aluno.getId()).orElseThrow();
    }

    protected void assertPersistedAlunoToMatchAllProperties(Aluno expectedAluno) {
        assertAlunoAllPropertiesEquals(expectedAluno, getPersistedAluno(expectedAluno));
    }

    protected void assertPersistedAlunoToMatchUpdatableProperties(Aluno expectedAluno) {
        assertAlunoAllUpdatablePropertiesEquals(expectedAluno, getPersistedAluno(expectedAluno));
    }
}
