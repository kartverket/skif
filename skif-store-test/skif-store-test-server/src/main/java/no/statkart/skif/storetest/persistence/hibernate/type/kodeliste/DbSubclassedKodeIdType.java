package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain.kodeliste.impl.DbSubclassedKodeId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class DbSubclassedKodeIdType extends KodeIdType {
    @Override
    public Class returnedClass() {
        return DbSubclassedKodeId.class;
    }
}


