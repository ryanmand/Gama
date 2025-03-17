package com.alunometa.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class MetaCriteriaTest {

    @Test
    void newMetaCriteriaHasAllFiltersNullTest() {
        var metaCriteria = new MetaCriteria();
        assertThat(metaCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void metaCriteriaFluentMethodsCreatesFiltersTest() {
        var metaCriteria = new MetaCriteria();

        setAllFilters(metaCriteria);

        assertThat(metaCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void metaCriteriaCopyCreatesNullFilterTest() {
        var metaCriteria = new MetaCriteria();
        var copy = metaCriteria.copy();

        assertThat(metaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(metaCriteria)
        );
    }

    @Test
    void metaCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var metaCriteria = new MetaCriteria();
        setAllFilters(metaCriteria);

        var copy = metaCriteria.copy();

        assertThat(metaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(metaCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var metaCriteria = new MetaCriteria();

        assertThat(metaCriteria).hasToString("MetaCriteria{}");
    }

    private static void setAllFilters(MetaCriteria metaCriteria) {
        metaCriteria.id();
        metaCriteria.valor();
        metaCriteria.area();
        metaCriteria.descricao();
        metaCriteria.alunoId();
        metaCriteria.distinct();
    }

    private static Condition<MetaCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getValor()) &&
                condition.apply(criteria.getArea()) &&
                condition.apply(criteria.getDescricao()) &&
                condition.apply(criteria.getAlunoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<MetaCriteria> copyFiltersAre(MetaCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getValor(), copy.getValor()) &&
                condition.apply(criteria.getArea(), copy.getArea()) &&
                condition.apply(criteria.getDescricao(), copy.getDescricao()) &&
                condition.apply(criteria.getAlunoId(), copy.getAlunoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
