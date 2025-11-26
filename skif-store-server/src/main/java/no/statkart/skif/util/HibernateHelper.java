package no.statkart.skif.util;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;

/**
 * @author Henrik Fredholm
 */
public class HibernateHelper {
    public static String getTableName(Session session, Class<?> cls) {
        SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
        MappingMetamodel mappingMetamodel = sfi.getMappingMetamodel();
        EntityPersister persister = mappingMetamodel.getEntityDescriptor(cls);

        if (!(persister instanceof SingleTableEntityPersister)) {
            throw new IllegalArgumentException("Entity " + cls + " is not mapped to a single table");
        }

        return ((SingleTableEntityPersister)persister).getTableName();
    }
}
