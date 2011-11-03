package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.BubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface KodeId<T extends Kode> extends BubbleId<T> {
    public Object getValue();
    public SnapshotVersion getSnapshotVersion();
    public KodelisteImplId getKodelisteId();
}
