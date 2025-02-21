package no.statkart.skif.store.relation.cache.annotation;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotasjon som brukes til å knytte relasjoner og invers relasjoner sammen.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@BindingAnnotation
@Retention(RUNTIME)
public @interface Relation {
    RelationType type() default RelationType.INVERSE;
    Cardinality cardinality();
    String name();
}
