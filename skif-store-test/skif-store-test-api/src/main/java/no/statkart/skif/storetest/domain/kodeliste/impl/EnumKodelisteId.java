package no.statkart.skif.storetest.domain.kodeliste.impl;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumBubbleKodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.KodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class EnumKodelisteId extends KodelisteId<EnumKodeliste> implements EnumBubbleKodelisteId<EnumKodeliste> {

    public EnumKodelisteId(long value) {
        super(new Long(value));
    }
    public EnumKodelisteId(Long value) {
        super(value);
    }

    public EnumKodelisteId(String value) {
        super(Long.parseLong(value));
    }
    
    public EnumKodelisteId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}