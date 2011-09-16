package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain.kodeliste.TestBDbKodeId;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBDbKodeIdType extends KodeIdType {
    @Override
    public Class returnedClass() {
        return TestBDbKodeId.class;
    }
}


