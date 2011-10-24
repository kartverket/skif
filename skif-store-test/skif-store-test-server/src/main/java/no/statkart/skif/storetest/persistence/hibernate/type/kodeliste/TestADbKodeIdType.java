package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain.demo.koder.TestADbKodeId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestADbKodeIdType extends KodeIdType {
    @Override
    public Class returnedClass() {
        return TestADbKodeId.class;
    }
}


