package no.statkart.skif.storetest.persistence.hibernate.type.kodeliste;

import no.statkart.skif.storetest.domain.demo.koder.ADbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.CDbKodeId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class CDbKodeIdType extends KodeIdType {
    @Override
    public Class returnedClass() {
        return CDbKodeId.class;
    }
}


