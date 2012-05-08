package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.RettsstiftelsestypeKodeId;
import no.statkart.skif.storetest.persistence.hibernate.type.kodeliste.KodeIdType;

/**
 * @author rorchr
 */
public class RettsstiftelsestypeKodeIdType extends KodeIdType {
    @Override
    public Class returnedClass() {
        return RettsstiftelsestypeKodeId.class;
    }
}


