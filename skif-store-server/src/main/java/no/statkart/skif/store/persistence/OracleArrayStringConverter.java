package no.statkart.skif.store.persistence;

import java.sql.Array;
import java.sql.Connection;
import java.util.Collection;

/**
 */
public class OracleArrayStringConverter extends OracleArrayConverter<String> {
    public OracleArrayStringConverter() {
        super(ORACLE_STRING_LIST_TYPE);
    }

    @Override
    public Array toArray(Connection sqlConnection, Object[] objects) {
        return super.toArray(sqlConnection, objects);
    }

    @Override
    protected Object[] toObjectArray(Connection sqlConnection, Collection<? extends String> objects) {
        return objects.toArray();
    }
}
