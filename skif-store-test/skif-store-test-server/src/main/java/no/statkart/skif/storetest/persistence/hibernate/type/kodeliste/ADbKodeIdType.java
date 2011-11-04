package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain.demo.koder.ADbKodeId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class ADbKodeIdType extends KodeIdType {
    @Override
    public Class returnedClass() {
        return ADbKodeId.class;
    }
}


