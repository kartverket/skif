package no.statkart.skif.util;

import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.Queryable;

/**
 * @author Henrik Fredholm
 */
public class HibernateHelper {

    public static ClassMetadata getClassMetadata(Session session, Class c) {
        return ((MetamodelImplementor) session.getSessionFactory().getMetamodel())
                .entityPersister(c)
                .getClassMetadata();
    }

    public static String getDiscriminatorSql(ClassMetadata classMetadata, String alias ) {
        if (!(classMetadata instanceof Queryable) || !(classMetadata instanceof AbstractEntityPersister)) {
            return "";
        }
        AbstractEntityPersister persister = (AbstractEntityPersister) classMetadata;
        String discriminatorValue = ((Queryable) classMetadata).getDiscriminatorSQLValue();
        String discriminatorColumn = persister.getDiscriminatorColumnName();
        if (discriminatorValue == null || discriminatorColumn == null || discriminatorColumn.isEmpty()) {
            return "";
        }
        return " and " + alias + "." + discriminatorColumn + "=" + discriminatorValue;
    }

    public static String getTableName(ClassMetadata classMetadata) {
        return ((AbstractEntityPersister) classMetadata).getRootTableName();
    }

}
