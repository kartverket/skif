package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain.demo.koder.BDbKodeId;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BDbKodeIdType extends KodeIdType {
    @Override
    public Class returnedClass() {
        return BDbKodeId.class;
    }
}


