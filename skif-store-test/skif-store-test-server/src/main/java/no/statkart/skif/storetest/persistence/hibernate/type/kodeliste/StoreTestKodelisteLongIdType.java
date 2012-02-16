package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.store.persistence.hibernate.type.KodelisteLongIdType;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestKodelisteLongIdType extends KodelisteLongIdType {
    @Override
    public Class returnedClass() {
        return StoreTestKodelisteLongId.class;
    }
}


