package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.SnapshotVersion;

/**
 * BubbleId baseklasse for Bubble baserte enumerations. Hver subklasse av denne klasse definere en enumerasjon av
 * id'er  og tilhørende bobble objekt.

 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class DbKodeId<T extends DbKode> extends KodeId<T> {

    protected DbKodeId(Object value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
