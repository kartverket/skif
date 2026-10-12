package no.statkart.skif.store.persistence.hibernate;

import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.metamodel.spi.MappingMetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;

/**
 * @author Henrik Fredholm
 */
final class HibernateHelper {

    /**
     * Finner {@code EntityPersister} for et gitt objekt. Denne er å foretrekke fremfor {@code {@link #getClassMetadata(Session, Class)}}
     * og å gjøre tilsvarende manuelt, da den innholder logikk for å håndtere proxy-objekter.
     *
     * <p>Fra og med Hibernate 7 er {@code org.hibernate.metadata.ClassMetadata} fjernet, så
     * {@code EntityPersister} brukes direkte som metadata-kilde (slik {@code ClassMetadata} tidligere ga tilgang til).
     */
    public static EntityPersister getClassMetadata(Session session, Object o) {
        Class c;
        if (o instanceof HibernateProxy) {
            c = ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass();
        } else {
            c = o.getClass();
        }
        return getClassMetadata(session, c);
    }

    public static EntityPersister getClassMetadata(Session session, Class c) {
        try {
            return ((MappingMetamodelImplementor) session.getSessionFactory().getMetamodel())
                .getEntityDescriptor(c);
        } catch (MappingException e) {
            // TODO: Finn bedre workaround. Må vite på forhånd om klassen er mappet. Må cache kanskje opp mappet klasser via getAllEntityNames()
            return null;
        }
    }

    public static boolean erAvTypeSomIkkeSkalInitialiseresVidere(EntityPersister classMetadata) {
        return !(classMetadata instanceof EntityPersister);
    }

}
