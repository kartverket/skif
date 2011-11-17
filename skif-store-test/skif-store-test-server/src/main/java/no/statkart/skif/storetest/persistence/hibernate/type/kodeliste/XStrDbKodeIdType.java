package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain.demo.koder.XStrDbKodeId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class XStrDbKodeIdType extends KodeIdType {
    @Override
    public Class returnedClass() {
        return XStrDbKodeId.class;
    }
}


