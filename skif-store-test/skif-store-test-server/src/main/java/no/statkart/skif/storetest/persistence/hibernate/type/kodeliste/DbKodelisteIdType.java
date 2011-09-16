package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class DbKodelisteIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return DbKodelisteId.class;
    }

    @Override
    protected Object createPrototypeId(Long value, ReplicaVersion replicaVersion) {
        return new DbKodelisteId(value, replicaVersion);
    }        
}


