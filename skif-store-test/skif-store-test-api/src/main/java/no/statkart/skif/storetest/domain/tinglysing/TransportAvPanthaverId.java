package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class TransportAvPanthaverId <T extends TransportAvPanthaver> extends TransportPaategningId<T>{
    public TransportAvPanthaverId(Long value) {
        super(value);
    }

    public TransportAvPanthaverId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
