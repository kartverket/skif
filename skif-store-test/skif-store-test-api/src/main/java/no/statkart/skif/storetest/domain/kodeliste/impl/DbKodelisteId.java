package no.statkart.skif.storetest.domain.kodeliste.impl;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbBubbleKodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.KodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class DbKodelisteId extends KodelisteId<DbKodeliste> implements DbBubbleKodelisteId<DbKodeliste> {

    public DbKodelisteId(long value) {
        super(new Long(value));
    }
    public DbKodelisteId(Long value) {
        super(value);
    }

    public DbKodelisteId(String value) {
        super(Long.parseLong(value));
    }

    public DbKodelisteId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}