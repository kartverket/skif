package no.statkart.skif.util;

import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.type.BasicType;

import java.util.Map;

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
     *
     * <p>Hibernate 7 har fjernet {@code Queryable}/{@code DeprecatedEntityStuff} og dermed
     * {@code AbstractEntityPersister#getDiscriminatorSQLValue()}. Verdien bygges her i stedet manuelt
     * ved å slå opp entitetens diskriminatorverdi og formatere den som SQL-literal via
     * {@code BasicType#getJdbcLiteralFormatter()}. IKKE verifisert ved kompilering/kjøring ennå - sjekk nøye
     * mot faktisk oppførsel når build kan kjøres.
     */
    @SuppressWarnings("unchecked")
    public static String getDiscriminatorSql(EntityPersister persister, String alias) {
        if (!(persister instanceof AbstractEntityPersister)) {
            throw new IllegalArgumentException("Not an AbstractEntityPersister: " + persister.getEntityName());
        }
        AbstractEntityPersister entityPersister = (AbstractEntityPersister) persister;
        if (entityPersister.needsDiscriminator()) {
            Object discriminatorValue = entityPersister.getSubclassByDiscriminatorValue().entrySet().stream()
                .filter(e -> e.getValue().equals(entityPersister.getEntityName()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Fant ikke diskriminatorverdi for " + entityPersister.getEntityName()));

            BasicType<Object> discriminatorType = (BasicType<Object>) entityPersister.getDiscriminatorType();
            Dialect dialect = entityPersister.getFactory().getJdbcServices().getDialect();
            String discriminatorSqlValue = discriminatorType.getJdbcLiteralFormatter().toJdbcLiteral(discriminatorValue, dialect, null);

            return " and " + alias + '.' + entityPersister.getDiscriminatorColumnName() + '=' + discriminatorSqlValue;
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
