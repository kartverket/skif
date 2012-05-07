package no.statkart.skif.storetest.persistence.hibernate.type.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.MatrikkelenhetsnivaaKodeId;
import no.statkart.skif.storetest.persistence.hibernate.type.kodeliste.KodeIdType;

/**
 * @author rorchr
 */
public class MatrikkelenhetsnivaaKodeIdType extends KodeIdType {
    @Override
    public Class returnedClass() {
        return MatrikkelenhetsnivaaKodeId.class;
    }
}


