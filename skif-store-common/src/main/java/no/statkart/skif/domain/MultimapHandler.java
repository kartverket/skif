package no.statkart.skif.domain;

import com.google.common.collect.Multimap;

import jakarta.annotation.Nullable;

/**
 * {@link Multimap#equals(Object)} kaller {@link Object#equals(Object)} på innholdet. Vi ønsker vanligvis ikke dette,
 * men heller bruke {@link EqualsByFields} for å sammenligne.
 * <p/>
 * Denne oppfyller kontrakten for {@link Multimap#equals(Object)} ved å delegere til bruk av {@link MapHandler}
 * på {@link Multimap#asMap()} pluss {@link MapHandler}.
 */
public class MultimapHandler implements EqualityHandler<Multimap<?, ?>> {
    @Override
    public boolean checkEquals(Multimap<?, ?> o1, @Nullable Object o2, EqualsByFields comparator) {
        if (o1 == o2) return true;
        if (!(o2 instanceof Multimap)) return false;

        Multimap<?, ?> m2 = (Multimap<?, ?>) o2;

        return comparator.isEqualByFields(o1.asMap(), m2.asMap());
    }
}
