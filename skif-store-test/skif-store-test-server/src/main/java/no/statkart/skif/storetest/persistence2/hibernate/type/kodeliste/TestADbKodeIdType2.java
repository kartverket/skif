package no.statkart.skif.storetest.persistence2.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain.kodeliste.TestADbKodeId;
import no.statkart.skif.storetest.persistence.hibernate.type.kodeliste.KodeIdType;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestADbKodeIdType2 extends KodeIdType2 {
    @Override
    public Class returnedClass() {
        return TestADbKodeId.class;
    }
}


