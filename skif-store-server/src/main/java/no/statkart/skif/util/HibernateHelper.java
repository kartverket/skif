package no.statkart.skif.util;

import no.statkart.skif.store.persistence.SessionSelector;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.proxy.HibernateProxy;

import java.sql.*;
import java.util.Collections;

/**
 * @author Henrik Fredholm
 */
public class HibernateHelper {
    public static void close(Statement statement) {
        JDBCHelper.close(statement);
    }

    public static void close(PreparedStatement preparedStatement, SessionSelector sessionSelector) {
        try {
            close(preparedStatement);
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    /**
     * Finner {@code ClassMetadata} for et gitt objekt. Denne er å foretrekke fremfor {@code {@link #getClassMetadata(Session, Class)}}
     * og å gjøre tilsvarende manuelt, da den innholder logikk for å håndtere proxy-objekter.
     */
    public static ClassMetadata getClassMetadata(Session session, Object o) {
        Class c;
        if (o instanceof HibernateProxy) {
            c = ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass();
        } else {
            c = o.getClass();
        }
        return getClassMetadata(session, c);
    }

    public static ClassMetadata getClassMetadata(Session session, Class c) {
        return session.getSessionFactory().getClassMetadata(c);
    }

    public static String getDiscriminatorSql(ClassMetadata classMetadata, String alias ) {
        return ((Queryable)classMetadata).filterFragment(alias, Collections.EMPTY_MAP);
    }

    public static String getTableName(ClassMetadata classMetadata) {
        return ((AbstractEntityPersister) classMetadata).getTableName();
    }

}
