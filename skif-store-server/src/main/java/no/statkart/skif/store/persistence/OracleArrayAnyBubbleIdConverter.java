package no.statkart.skif.store.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.persistence.hibernate.type.AnyBubbleIdType;

/**
 */
public class OracleArrayAnyBubbleIdConverter extends OracleArrayLongBubbleIdConverter {
    static AnyBubbleIdType anyBubbleIdType = new AnyBubbleIdType();
    public OracleArrayAnyBubbleIdConverter() {
        super(ORACLE_NUMBER_STRING_LIST_TYPE);
    }

    @Override
    protected Object toValue(BubbleId<?> object) {
        return anyBubbleIdType.toSQLValues(object);
    }
}
