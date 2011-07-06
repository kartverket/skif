package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.ReplicaVersion;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface BubbleKodeId<T extends BubbleKode> extends BubbleId<T> {
    public Object getValue();
    public ReplicaVersion getReplicaVersion();
    public BubbleKodelisteId getKodelisteId();
}
