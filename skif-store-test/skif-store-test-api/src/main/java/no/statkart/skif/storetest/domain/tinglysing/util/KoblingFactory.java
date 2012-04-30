package no.statkart.skif.storetest.domain.tinglysing.util;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface KoblingFactory<R,V> extends Serializable {
    Kobling<R, V> create(R rolle, V value);
}
