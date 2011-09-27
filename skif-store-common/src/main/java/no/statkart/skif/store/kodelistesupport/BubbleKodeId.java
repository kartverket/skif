package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIdInterface;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface BubbleKodeId<T extends BubbleKode> extends BubbleIdInterface<T> {
    public Object getValue();
    public SnapshotVersion getSnapshotVersion();
    public BubbleKodelisteId getKodelisteId();
}
