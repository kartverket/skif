package no.statkart.skif.util;

import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.Queryable;

import java.util.Collections;

/**
 * @author Henrik Fredholm
 */
public class HibernateHelper {

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
