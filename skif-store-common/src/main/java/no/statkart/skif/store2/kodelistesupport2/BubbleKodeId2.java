package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store2.BubbleId2;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BubbleKodeId2<T extends BubbleKode2> extends BubbleId2<T> {
    public Object getValue();
    public ReplicaVersion getReplicaVersion();
    public KodelisteId2 getKodelisteId();
}
