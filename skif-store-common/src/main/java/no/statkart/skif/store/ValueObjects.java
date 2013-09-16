package no.statkart.skif.store;

import java.util.Collection;
import java.util.Set;

/**
 * Hjelpeklasser for standardisert implementasjon av ValueObject funksjonalitet.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class ValueObjects {
    static public <E extends ValueObject> void setFrom(Collection<E> collection, Set<E> newElements) {
        collection.clear();
        collection.addAll(newElements);
    }

}
