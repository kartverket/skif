package no.statkart.skif.storetest.domain.multikobling.util;

import no.statkart.skif.store.BubbleId;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface KoblingFactory<R,V> extends Serializable {
    Kobling<R, V> create(R rolle, V value);
}
