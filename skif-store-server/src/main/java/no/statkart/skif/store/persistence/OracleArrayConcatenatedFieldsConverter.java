package no.statkart.skif.store.persistence;

import no.statkart.skif.store.ConcatenatedFields;
import no.statkart.skif.store.ConcatenatedFieldsSerialization;
import oracle.sql.ARRAY;

import java.sql.Connection;
import java.util.Collection;

/**
 */
public class OracleArrayConcatenatedFieldsConverter extends OracleArrayConverter<ConcatenatedFieldsSerialization> {
    public OracleArrayConcatenatedFieldsConverter() {
        super(ORACLE_STRING_STRING_LIST_TYPE);
    }

    @Override
    protected Object toValue(ConcatenatedFieldsSerialization object) {
        return new Object[] {object.toConcatinatedFields().getValue(), object.getClass().getName()};
    }
}
