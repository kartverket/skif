package no.statkart.skif.store.persistence;


import oracle.sql.ARRAY;

import java.sql.Connection;
import java.util.Collection;

/**
 */
public class OracleArrayNumberConverter extends OracleArrayConverter<Number> {
    public OracleArrayNumberConverter() {
        super(ORACLE_NUMBER_LIST_TYPE);
    }

    @Override
    public ARRAY toArray(Connection sqlConnection, Object[] objects) {
        return super.toArray(sqlConnection, objects);
    }

    @Override
    protected Object[] toObjectArray(Connection sqlConnection, Collection<? extends Number> objects) {
        return objects.toArray();
    }
}
