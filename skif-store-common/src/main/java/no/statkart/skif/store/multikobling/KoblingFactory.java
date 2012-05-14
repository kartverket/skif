package no.statkart.skif.store.multikobling;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface KoblingFactory<R,V, K extends Kobling<R,V>> extends Serializable {
    K create(R rolle, V value);
}
