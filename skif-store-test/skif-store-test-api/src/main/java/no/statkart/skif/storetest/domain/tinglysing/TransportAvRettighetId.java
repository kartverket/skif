package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class TransportAvRettighetId <T extends TransportAvRettighet> extends TransportPaategningId<T>{
    public TransportAvRettighetId(Long value) {
        super(value);
    }

    public TransportAvRettighetId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
