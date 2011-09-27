package no.statkart.skif.store2.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.hibernate.BugFixDeleteEventListener;
import no.statkart.skif.store2.ReplicaVersion2;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.DeleteEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Builder som opprette standard Hibernate SessionFactory
 *
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryBuilder2 {
    private static final Logger logger = LoggerFactory.getLogger(HibernateSessionFactoryBuilder2.class);
    protected final List<String> hbmResource = new ArrayList<String>();
    protected final String mappingFilesDirectory;
    protected final Properties hibernateProperties;
    protected Interceptor interceptor;


    public HibernateSessionFactoryBuilder2(Properties hibernateProperties, String mappingFilesDirectory) {
        this.hibernateProperties = hibernateProperties;
        if (!mappingFilesDirectory.endsWith("/")) {
            mappingFilesDirectory += "/";
        }
        this.mappingFilesDirectory = mappingFilesDirectory;
    }

    public HibernateSessionFactoryBuilder2 addResource(Class clazz) {
        addResourceUsingAbsolutePath(clazz, mappingFilesDirectory + clazz.getSimpleName() + ".hbm.xml");
        return this;
    }

    public HibernateSessionFactoryBuilder2 addResourceUsingRelativePath(String relativePath, Class clazz) {
        addResourceUsingAbsolutePath(clazz, mappingFilesDirectory + relativePath + "/" + clazz.getSimpleName() + ".hbm.xml");
        return this;
    }

    public HibernateSessionFactoryBuilder2 addResourceWithSubclasses(Class baseclass, Class... subclasses) {
        hbmResource.add(mappingFilesDirectory + baseclass.getSimpleName() + ".hbm.xml");
        return this;
    }

    public HibernateSessionFactoryBuilder2 addResourceWithSubclassesUsingRelativePath(String relativePath, Class baseclass, Class... subclasses) {
        hbmResource.add(mappingFilesDirectory + relativePath + "/" + baseclass.getSimpleName() + ".hbm.xml");
        return this;
    }

    public HibernateSessionFactoryBuilder2 addResourceUsingAbsolutePath(Class clazz, String hbmFilename) {
        hbmResource.add(hbmFilename);
        return this;
    }

    public SessionFactory build(ReplicaVersion2 key) {
        SessionFactory sessionFactory = null;
        logger.debug("creating session factory");
        try {
            Configuration cfg = createConfiguration(hibernateProperties);
            sessionFactory = cfg.buildSessionFactory();
        } catch (HibernateException e) {
            throw new ImplementationException("Feil ved initialisering av hibernate", e, logger);
        }
        return sessionFactory;

    }

    protected Configuration createConfiguration(Properties props) {
        // Log databaseparametre. I singlevm mode brukes JDBCTransactionFactory (dvs url, bruker/password).
        // I servermode brukes JTATransactionFactory (dvs datasource)
        if (props.get("hibernate.transaction.factory_class").equals("org.hibernate.transaction.JDBCTransactionFactory")) {
            logger.info("Hibernatekonfigurasjon: " + props.get("hibernate.connection.url") + " - " + props.get("hibernate.connection.username"));
        } else {
            logger.info("Hibernatekonfigurasjon: " + props.get("hibernate.connection.datasource"));
        }
        ClassLoader cl = HibernateSessionFactoryBuilder2.class.getClassLoader();
        Configuration cfg = null;
        try {
            cfg = new Configuration().setProperties(props);
            for (String hbm : hbmResource) {
                cfg.addResource(hbm, cl);
            }
            // Legg in patch for Hibernate 3.2.6
            DeleteEventListener[] deleteEventStack = {new BugFixDeleteEventListener()};
            cfg.getEventListeners().setDeleteEventListeners(deleteEventStack);
        } catch (MappingException e) {
            throw new ImplementationException("Feil i hibernate mapping-filer: " + e.getMessage(), e, logger);
        }

        if (interceptor != null) {
            cfg.setInterceptor(interceptor);
        }
        return cfg;
    }
}
