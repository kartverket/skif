package no.statkart.skif.store.persistence;

import oracle.sql.ARRAY;

import java.sql.Connection;
import java.util.Collection;

/**
 */
public class OracleArrayStringStringConverter extends OracleArrayConverter<Object[]> {
    public OracleArrayStringStringConverter() {
        super(ORACLE_STRING_STRING_LIST_TYPE);
    }

    @Override
    public ARRAY toArray(Connection sqlConnection, Object[] objects) {
        return super.toArray(sqlConnection, objects);
    }

    @Override
    protected Object[] toObjectArray(Connection sqlConnection, Collection<? extends Object[]> objects) {
        return objects.toArray();
    }
}
