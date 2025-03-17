package com.alunometa.service.criteria;

import com.alunometa.domain.enumeration.AreaDoEnem;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.alunometa.domain.Meta} entity. This class is used
 * in {@link com.alunometa.web.rest.MetaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /metas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MetaCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AreaDoEnem
     */
    public static class AreaDoEnemFilter extends Filter<AreaDoEnem> {

        public AreaDoEnemFilter() {}

        public AreaDoEnemFilter(AreaDoEnemFilter filter) {
            super(filter);
        }

        @Override
        public AreaDoEnemFilter copy() {
            return new AreaDoEnemFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter valor;

    private AreaDoEnemFilter area;

    private StringFilter descricao;

    private LongFilter alunoId;

    private Boolean distinct;

    public MetaCriteria() {}

    public MetaCriteria(MetaCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.valor = other.optionalValor().map(IntegerFilter::copy).orElse(null);
        this.area = other.optionalArea().map(AreaDoEnemFilter::copy).orElse(null);
        this.descricao = other.optionalDescricao().map(StringFilter::copy).orElse(null);
        this.alunoId = other.optionalAlunoId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MetaCriteria copy() {
        return new MetaCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getValor() {
        return valor;
    }

    public Optional<IntegerFilter> optionalValor() {
        return Optional.ofNullable(valor);
    }

    public IntegerFilter valor() {
        if (valor == null) {
            setValor(new IntegerFilter());
        }
        return valor;
    }

    public void setValor(IntegerFilter valor) {
        this.valor = valor;
    }

    public AreaDoEnemFilter getArea() {
        return area;
    }

    public Optional<AreaDoEnemFilter> optionalArea() {
        return Optional.ofNullable(area);
    }

    public AreaDoEnemFilter area() {
        if (area == null) {
            setArea(new AreaDoEnemFilter());
        }
        return area;
    }

    public void setArea(AreaDoEnemFilter area) {
        this.area = area;
    }

    public StringFilter getDescricao() {
        return descricao;
    }

    public Optional<StringFilter> optionalDescricao() {
        return Optional.ofNullable(descricao);
    }

    public StringFilter descricao() {
        if (descricao == null) {
            setDescricao(new StringFilter());
        }
        return descricao;
    }

    public void setDescricao(StringFilter descricao) {
        this.descricao = descricao;
    }

    public LongFilter getAlunoId() {
        return alunoId;
    }

    public Optional<LongFilter> optionalAlunoId() {
        return Optional.ofNullable(alunoId);
    }

    public LongFilter alunoId() {
        if (alunoId == null) {
            setAlunoId(new LongFilter());
        }
        return alunoId;
    }

    public void setAlunoId(LongFilter alunoId) {
        this.alunoId = alunoId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MetaCriteria that = (MetaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(valor, that.valor) &&
            Objects.equals(area, that.area) &&
            Objects.equals(descricao, that.descricao) &&
            Objects.equals(alunoId, that.alunoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, valor, area, descricao, alunoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetaCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalValor().map(f -> "valor=" + f + ", ").orElse("") +
            optionalArea().map(f -> "area=" + f + ", ").orElse("") +
            optionalDescricao().map(f -> "descricao=" + f + ", ").orElse("") +
            optionalAlunoId().map(f -> "alunoId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
