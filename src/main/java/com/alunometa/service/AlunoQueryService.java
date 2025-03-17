package com.alunometa.service;

import com.alunometa.domain.*; // for static metamodels
import com.alunometa.domain.Aluno;
import com.alunometa.repository.AlunoRepository;
import com.alunometa.service.criteria.AlunoCriteria;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Aluno} entities in the database.
 * The main input is a {@link AlunoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Aluno} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AlunoQueryService extends QueryService<Aluno> {

    private static final Logger LOG = LoggerFactory.getLogger(AlunoQueryService.class);

    private final AlunoRepository alunoRepository;

    public AlunoQueryService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    /**
     * Return a {@link Page} of {@link Aluno} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Aluno> findByCriteria(AlunoCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Aluno> specification = createSpecification(criteria);
        return alunoRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AlunoCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Aluno> specification = createSpecification(criteria);
        return alunoRepository.count(specification);
    }

    /**
     * Function to convert {@link AlunoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Aluno> createSpecification(AlunoCriteria criteria) {
        Specification<Aluno> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Aluno_.id));
            }
            if (criteria.getNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNome(), Aluno_.nome));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Aluno_.email));
            }
            if (criteria.getDataNascimento() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataNascimento(), Aluno_.dataNascimento));
            }
            if (criteria.getTelefone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTelefone(), Aluno_.telefone));
            }
            if (criteria.getMetasId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getMetasId(), root -> root.join(Aluno_.metas, JoinType.LEFT).get(Meta_.id))
                );
            }
        }
        return specification;
    }
}
