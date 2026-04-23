package no.statkart.skif.domain;

import jakarta.annotation.Nullable;
import java.util.Map;

/**
 * {@link Map#equals(Object)} kaller {@link Object#equals(Object)} på {@link Map.Entry}. Vi ønsker vanligvis ikke dette,
 * men heller bruke {@link EqualsByFields} for å sammenligne.
 * <p/>
 * Denne oppfyller kontrakten for {@link Map#equals(Object)} ved å delegere til bruk av {@link SetHandler} på
 * {@link Map#entrySet()} pluss {@link MapEntryHandler}.
 */
public class MapHandler implements EqualityHandler<Map<?, ?>> {
    @Override
    public boolean checkEquals(Map<?, ?> o1, @Nullable Object o2, EqualsByFields comparator) {
        if (o1 == o2) return true;
        if (!(o2 instanceof Map)) return false;

        Map<?, ?> m2 = (Map<?, ?>) o2;

        return comparator.isEqualByFields(o1.entrySet(), m2.entrySet());
    }
}
