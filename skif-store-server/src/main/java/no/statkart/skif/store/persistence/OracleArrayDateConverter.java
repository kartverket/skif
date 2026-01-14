package no.statkart.skif.store.persistence;

import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.util.Collection;

/**
 */
public class OracleArrayDateConverter extends OracleArrayConverter<Date> {
    public OracleArrayDateConverter() {
        super(ORACLE_DATE_LIST_TYPE);
    }

    @Override
    public Array toArray(Connection sqlConnection, Object[] objects) {
        return super.toArray(sqlConnection, objects);
    }

    @Override
    protected Object[] toObjectArray(Connection sqlConnection, Collection<? extends Date> objects) {
        return objects.toArray();
    }
}
