package no.statkart.skif.storetest.persistence2.hibernate.type.kodeliste;

import no.statkart.skif.store2.kodelistesupport2.DbSubclassedKodeId2;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbSubclassedKodeId;
import no.statkart.skif.storetest.persistence.hibernate.type.kodeliste.KodeIdType;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class DbSubclassedKodeIdType2 extends KodeIdType2 {
    @Override
    public Class returnedClass() {
        return DbSubclassedKodeId2.class;
    }
}


