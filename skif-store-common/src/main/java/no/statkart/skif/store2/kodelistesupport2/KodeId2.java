package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store2.BubbleId2;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface KodeId2<T extends Kode2> extends BubbleId2<T> {
    public Object getValue();
    public SnapshotVersion getReplicaVersion();
    public KodelisteId2 getKodelisteId();
}
