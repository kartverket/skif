package no.statkart.skif.storetest.persistence2.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain.kodeliste.TestBDbKodeId;
import no.statkart.skif.storetest.domain2.kodeliste.TestBDbKodeId2;
import no.statkart.skif.storetest.persistence.hibernate.type.kodeliste.KodeIdType;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBDbKodeIdType2 extends KodeIdType2 {
    @Override
    public Class returnedClass() {
        return TestBDbKodeId2.class;
    }
}


