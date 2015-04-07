package no.statkart.skif.store.persistence;

import no.statkart.skif.store.BubbleId;

/**
 */
public class OracleArrayAnyBubbleIdConverter extends OracleArrayLongBubbleIdConverter {
    public OracleArrayAnyBubbleIdConverter() {
        super(ORACLE_NUMBER_STRING_LIST_TYPE);
    }

    @Override
    protected Object toValue(BubbleId<?> object) {
        return new Object[] {object.getValue(), object.getClass().getName()};
    }
}
