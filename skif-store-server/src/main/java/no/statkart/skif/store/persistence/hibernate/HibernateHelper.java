package no.statkart.skif.store.persistence.hibernate;

import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.proxy.HibernateProxy;

import java.util.Collections;

/**
 * @author Henrik Fredholm
 */
public class HibernateHelper {

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

    public static MetamodelImplementor getMetamodel(Session session) {
        return (MetamodelImplementor) session.getSessionFactory().getMetamodel();
    }

    public static ClassMetadata getClassMetadata(Session session, Class c) {
        try {
            return (ClassMetadata) getMetamodel(session).entityPersister(c.getName());
        } catch (MappingException e) {
            // TODO: Finn bedre workaround. Må vite på forhånd om klassen er mappet. Må cache kanskje opp mappet klasser via getAllEntityNames()
            return null;
        }
    }

    public static String getDiscriminatorSql(ClassMetadata classMetadata, String alias ) {
        return ((Queryable)classMetadata).filterFragment(alias, Collections.EMPTY_MAP);
    }

    public static String getTableName(ClassMetadata classMetadata) {
        return ((AbstractEntityPersister) classMetadata).getTableName();
    }

    public static boolean erAvTypeSomIkkeSkalInitialiseresVidere(ClassMetadata classMetadata) {
        return (!(classMetadata instanceof EntityPersister));
    }


}
