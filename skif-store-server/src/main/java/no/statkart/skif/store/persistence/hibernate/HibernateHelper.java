package no.statkart.skif.store.persistence.hibernate;

import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;

/**
 * @author Henrik Fredholm
 */
final class HibernateHelper {

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
        try {
            return ((MetamodelImplementor) session.getSessionFactory().getMetamodel())
                .entityPersister(c)
                .getClassMetadata();
        } catch (MappingException e) {
            // TODO: Finn bedre workaround. Må vite på forhånd om klassen er mappet. Må cache kanskje opp mappet klasser via getAllEntityNames()
            return null;
        }
    }

    public static boolean erAvTypeSomIkkeSkalInitialiseresVidere(ClassMetadata classMetadata) {
        return !(classMetadata instanceof EntityPersister);
    }

}
