package com.alunometa.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AlunoCriteriaTest {

    @Test
    void newAlunoCriteriaHasAllFiltersNullTest() {
        var alunoCriteria = new AlunoCriteria();
        assertThat(alunoCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void alunoCriteriaFluentMethodsCreatesFiltersTest() {
        var alunoCriteria = new AlunoCriteria();

        setAllFilters(alunoCriteria);

        assertThat(alunoCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void alunoCriteriaCopyCreatesNullFilterTest() {
        var alunoCriteria = new AlunoCriteria();
        var copy = alunoCriteria.copy();

        assertThat(alunoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(alunoCriteria)
        );
    }

    @Test
    void alunoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var alunoCriteria = new AlunoCriteria();
        setAllFilters(alunoCriteria);

        var copy = alunoCriteria.copy();

        assertThat(alunoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(alunoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var alunoCriteria = new AlunoCriteria();

        assertThat(alunoCriteria).hasToString("AlunoCriteria{}");
    }

    private static void setAllFilters(AlunoCriteria alunoCriteria) {
        alunoCriteria.id();
        alunoCriteria.nome();
        alunoCriteria.email();
        alunoCriteria.dataNascimento();
        alunoCriteria.telefone();
        alunoCriteria.metasId();
        alunoCriteria.distinct();
    }

    private static Condition<AlunoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNome()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getDataNascimento()) &&
                condition.apply(criteria.getTelefone()) &&
                condition.apply(criteria.getMetasId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AlunoCriteria> copyFiltersAre(AlunoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNome(), copy.getNome()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getDataNascimento(), copy.getDataNascimento()) &&
                condition.apply(criteria.getTelefone(), copy.getTelefone()) &&
                condition.apply(criteria.getMetasId(), copy.getMetasId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
