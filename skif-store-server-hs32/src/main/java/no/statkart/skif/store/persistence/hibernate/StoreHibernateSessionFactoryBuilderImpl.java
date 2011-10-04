package no.statkart.skif.store.persistence.hibernate;

import java.util.Properties;

/**
 * @author Henrik Fredholm
 */
public class StoreHibernateSessionFactoryBuilderImpl extends StoreHibernateSessionFactoryBuilder {
    public StoreHibernateSessionFactoryBuilderImpl(Properties hibernateProperties, String mappingFilesDirectory) {
        super(hibernateProperties, mappingFilesDirectory);
    }
}
