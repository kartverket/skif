package no.statkart.skif.storetest.domain.kodeliste.impl;

import no.statkart.skif.store.kodelistesupport.*;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class DbKodeSupport extends DbBubbleKodeSupport<DbKodeliste, DbKodelisteId> {
    public DbKodeSupport(Class<? extends DbKodeId<?>> idClass, long kodelisteIdValue) {
        super(idClass, new DbKodelisteId(kodelisteIdValue));
    }
}
