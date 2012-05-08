package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.RettstypeKodeId;
import no.statkart.skif.storetest.persistence.hibernate.type.kodeliste.KodeIdType;

/**
 * @author rorchr
 */
public class RettstypeKodeIdType extends KodeIdType {
    @Override
    public Class returnedClass() {
        return RettstypeKodeId.class;
    }
}


