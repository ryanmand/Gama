package com.alunometa.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.alunometa.domain.Aluno} entity. This class is used
 * in {@link com.alunometa.web.rest.AlunoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /alunos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlunoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private StringFilter email;

    private LocalDateFilter dataNascimento;

    private StringFilter telefone;

    private LongFilter metasId;

    private Boolean distinct;

    public AlunoCriteria() {}

    public AlunoCriteria(AlunoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nome = other.optionalNome().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.dataNascimento = other.optionalDataNascimento().map(LocalDateFilter::copy).orElse(null);
        this.telefone = other.optionalTelefone().map(StringFilter::copy).orElse(null);
        this.metasId = other.optionalMetasId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AlunoCriteria copy() {
        return new AlunoCriteria(this);
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

    public StringFilter getNome() {
        return nome;
    }

    public Optional<StringFilter> optionalNome() {
        return Optional.ofNullable(nome);
    }

    public StringFilter nome() {
        if (nome == null) {
            setNome(new StringFilter());
        }
        return nome;
    }

    public void setNome(StringFilter nome) {
        this.nome = nome;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public LocalDateFilter getDataNascimento() {
        return dataNascimento;
    }

    public Optional<LocalDateFilter> optionalDataNascimento() {
        return Optional.ofNullable(dataNascimento);
    }

    public LocalDateFilter dataNascimento() {
        if (dataNascimento == null) {
            setDataNascimento(new LocalDateFilter());
        }
        return dataNascimento;
    }

    public void setDataNascimento(LocalDateFilter dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public StringFilter getTelefone() {
        return telefone;
    }

    public Optional<StringFilter> optionalTelefone() {
        return Optional.ofNullable(telefone);
    }

    public StringFilter telefone() {
        if (telefone == null) {
            setTelefone(new StringFilter());
        }
        return telefone;
    }

    public void setTelefone(StringFilter telefone) {
        this.telefone = telefone;
    }

    public LongFilter getMetasId() {
        return metasId;
    }

    public Optional<LongFilter> optionalMetasId() {
        return Optional.ofNullable(metasId);
    }

    public LongFilter metasId() {
        if (metasId == null) {
            setMetasId(new LongFilter());
        }
        return metasId;
    }

    public void setMetasId(LongFilter metasId) {
        this.metasId = metasId;
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
        final AlunoCriteria that = (AlunoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(email, that.email) &&
            Objects.equals(dataNascimento, that.dataNascimento) &&
            Objects.equals(telefone, that.telefone) &&
            Objects.equals(metasId, that.metasId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, email, dataNascimento, telefone, metasId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlunoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNome().map(f -> "nome=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalDataNascimento().map(f -> "dataNascimento=" + f + ", ").orElse("") +
            optionalTelefone().map(f -> "telefone=" + f + ", ").orElse("") +
            optionalMetasId().map(f -> "metasId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
