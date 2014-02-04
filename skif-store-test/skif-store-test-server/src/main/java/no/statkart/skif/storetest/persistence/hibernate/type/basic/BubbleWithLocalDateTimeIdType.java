package no.statkart.skif.storetest.persistence.hibernate.type.basic;

import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.basic.BubbleWithLocalDateTimeId;

/**
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class BubbleWithLocalDateTimeIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return BubbleWithLocalDateTimeId.class;
    }
}
