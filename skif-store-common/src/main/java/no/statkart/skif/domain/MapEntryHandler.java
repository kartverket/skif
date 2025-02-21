package no.statkart.skif.domain;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * {@link Map.Entry#equals(Object)} kaller {@link Object#equals(Object)} på {@link Map.Entry#getKey()}
 * og {@link Map.Entry#getValue()} ()}. Vi ønsker vanligvis ikke dette, men heller bruke {@link EqualsByFields} for å
 * sammenligne.
 */
public class MapEntryHandler implements EqualityHandler<Map.Entry<?, ?>> {
    @Override
    public boolean checkEquals(Map.Entry<?, ?> o1, @Nullable Object o2, EqualsByFields comparator) {
        if (o1 == o2) return true;
        if (!(o2 instanceof Map.Entry)) return false;

        Map.Entry<?, ?> e2 = (Map.Entry<?, ?>) o2;

        return comparator.isEqualByFields(o1.getKey(), e2.getKey())
                && comparator.isEqualByFields(o1.getValue(), e2.getValue());
    }
}
