package no.statkart.skif.storetest2.persistence.hibernate.type.kodeliste;

import no.statkart.skif.store.persistence.hibernate.type.KodelisteLongIdType;
import no.statkart.skif.storetest2.domain.kodeliste.StoreTest2DbKodeId;

/**
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
public class StoreTest2DbKodeIdType extends KodelisteLongIdType {
    @Override
    public Class returnedClass() {
        return StoreTest2DbKodeId.class;
    }
}


