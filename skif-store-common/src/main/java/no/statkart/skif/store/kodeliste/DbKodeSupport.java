package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class DbKodeSupport<I extends KodeId, KL extends Kodeliste, KLID extends KodelisteId<KL>> {
    private final Class<I> kodeIdClass;
    private final KLID kodelisteId;

    public DbKodeSupport(Class<I> kodeIdClass, KLID kodelisteId) {
        this.kodeIdClass = kodeIdClass;
        this.kodelisteId = kodelisteId;
    }

    public I defineId(Object idValue) {
        return BubbleIds.createInstance(kodeIdClass, idValue, SnapshotVersion.CURRENT);
    }


    public KLID getKodelisteId() {
        return kodelisteId;
    }
}
