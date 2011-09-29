package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain.kodeliste.TestDbSubclassedKodeIdImpl;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestDbSubclassedKodeIdType extends KodeIdType {
    @Override
    public Class returnedClass() {
        return TestDbSubclassedKodeIdImpl.class;
    }
}


