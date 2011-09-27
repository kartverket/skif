package no.statkart.skif.storetest.persistence2.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain2.kodeliste.TestADbKodeId2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestADbKodeIdType2 extends KodeIdType2 {
    @Override
    public Class returnedClass() {
        return TestADbKodeId2.class;
    }
}


