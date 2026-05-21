package no.statkart.skif.util;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;

/**
 * @author Henrik Fredholm
 */
public class HibernateHelper {
    public static EntityPersister getPersister(Session session, Class<?> cls) {
        SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
        MappingMetamodel mappingMetamodel = sfi.getMappingMetamodel();
        return mappingMetamodel.getEntityDescriptor(cls);
    }

    /**
     * Opsjonell WHERE-skranke til bruk i native SQL spørringer. 
     * @return tom streng dersom entiteten ikke har subklasser eller når baseklassen angis.
     */
    public static String getDiscriminatorSql(EntityPersister persister, String alias) {
        if (!(persister instanceof AbstractEntityPersister)) {
            throw new IllegalArgumentException("Not an AbstractEntityPersister: " + persister.getEntityName());
        }
        AbstractEntityPersister entityPersister = (AbstractEntityPersister) persister;
        if (entityPersister.needsDiscriminator()) {
            return " and " + alias + '.' + entityPersister.getDiscriminatorColumnName() + '=' + entityPersister.getDiscriminatorSQLValue();
        }
        return "";
    }
    
    public static String getTableName(Session session, Class<?> cls) {
        EntityPersister persister = getPersister(session, cls);

        if (!(persister instanceof SingleTableEntityPersister)) {
            throw new IllegalArgumentException("Entity " + cls + " is not mapped to a single table");
        }

        return ((SingleTableEntityPersister) persister).getTableName();
    }
}
