package com.alunometa.service;

import com.alunometa.domain.*; // for static metamodels
import com.alunometa.domain.Meta;
import com.alunometa.repository.MetaRepository;
import com.alunometa.service.criteria.MetaCriteria;
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
 * Service for executing complex queries for {@link Meta} entities in the database.
 * The main input is a {@link MetaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Meta} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MetaQueryService extends QueryService<Meta> {

    private static final Logger LOG = LoggerFactory.getLogger(MetaQueryService.class);

    private final MetaRepository metaRepository;

    public MetaQueryService(MetaRepository metaRepository) {
        this.metaRepository = metaRepository;
    }

    /**
     * Return a {@link Page} of {@link Meta} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Meta> findByCriteria(MetaCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Meta> specification = createSpecification(criteria);
        return metaRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MetaCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Meta> specification = createSpecification(criteria);
        return metaRepository.count(specification);
    }

    /**
     * Function to convert {@link MetaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Meta> createSpecification(MetaCriteria criteria) {
        Specification<Meta> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Meta_.id));
            }
            if (criteria.getValor() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValor(), Meta_.valor));
            }
            if (criteria.getArea() != null) {
                specification = specification.and(buildSpecification(criteria.getArea(), Meta_.area));
            }
            if (criteria.getDescricao() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescricao(), Meta_.descricao));
            }
            if (criteria.getAlunoId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAlunoId(), root -> root.join(Meta_.aluno, JoinType.LEFT).get(Aluno_.id))
                );
            }
        }
        return specification;
    }
}
