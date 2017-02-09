package no.statkart.skif.domain;

import no.statkart.skif.exception.ImplementationException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Alle klasser som implementerer dette interfacet må oppfylle følgende kontrakt:
 * <p/>
 * Dersom {@link #equalsByFields(Object, EqualsByFields)} returnerer {@code true}, så skal {@link #equals(Object)} også
 * returnere {@code true}. Det motsatte er ikke et krav. Det følger dermed også at dersom
 * {@code equalsByFields(Object, EqualsByFields)} returnerer {@code true}, så har de to objektene samme
 * {@linkplain #hashCode() hashcode}.
 */
public interface EqualityByFields {
    /**
     * Sammenligner dette objektet med {@code other}. Reglene for likehet må avklares mellom bruker og implementasjon.
     *
     * @param other      Andre objekt. OBS! Det er ingen garanti for at dette objektet er av samme klasse som o1.
     *                   Dette tilsvarer {@link Object#equals(Object)}
     * @param comparator Den sammenligneren som er i bruk. Man må fortsette å bruke denne for å kunne bruke de handlers den har i tillegg til denne.
     * @return {@code true} dersom objektene anses som like
     */
    default boolean equalsByFields(Object other, EqualsByFields comparator) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        try {
            for (Class c = getClass(); c != Object.class; c = c.getSuperclass()) {
                Field[] fields = c.getDeclaredFields();
                for (Field field : fields) {
                    int modifiers = field.getModifiers();
                    if (!(Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers))) {
                        field.setAccessible(true);
                        Object v1 = field.get(this);
                        Object v2 = field.get(other);

                        if (!comparator.isEqualByFields(v1, v2)) {
                            return false;
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new ImplementationException("Reflection feilet", e);
        }

        return true;
    }
}
