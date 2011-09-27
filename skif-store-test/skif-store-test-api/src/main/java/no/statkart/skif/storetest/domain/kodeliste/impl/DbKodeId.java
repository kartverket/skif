package no.statkart.skif.storetest.domain.kodeliste.impl;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbBubbleKodeId;
import no.statkart.skif.storetest.domain.kodeliste.KodeId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class DbKodeId<T extends DbKode> extends KodeId<T> implements DbBubbleKodeId<T> {

    public static <I extends DbKodeId<?>> DbKodeSupport getKodeSupport(Class<I> idClass) {
        return (DbKodeSupport) KodeId.getKodeSupport(idClass);
    }

    protected DbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected abstract DbKodeSupport getKodeSupport();

}
