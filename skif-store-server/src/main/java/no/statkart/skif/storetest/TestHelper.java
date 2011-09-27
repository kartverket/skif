package no.statkart.skif.storetest;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.persistence.ConnectionFactory;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.persistence.ConnectionFactoryManagerMultiVersionImpl;
import no.statkart.skif.persistence.JDBCConnectionFactory;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.StoreHibernateSessionFactoryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TestHelper {
    private TestHelper() {
    }

    public static  Configuration getSkifConfiguration() {
        return new PropertiesConfiguration("skif.properties");
    }

    public static HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new HibernateSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }

    public static StoreHibernateSessionFactoryBuilder createStoreHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new StoreHibernateSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }

    public static JDBCConnectionFactory createJDBCConnectionFactory() {
        return createJDBCConnectionFactory(getSkifConfiguration());
    }
    public static JDBCConnectionFactory createJDBCConnectionFactory(Configuration configuration) {
        String username = configuration.getString(ConfigurationConstants.DB_USERNAME);
        String password = configuration.getString(ConfigurationConstants.DB_PASSWORD);
        String sid = configuration.getString(ConfigurationConstants.DB_SID);
        String hostname = configuration.getString(ConfigurationConstants.DB_HOSTNAME);
        String port = configuration.getString(ConfigurationConstants.DB_PORT);
        String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);
        return new JDBCConnectionFactory(url, username, password);
    }

    public static ConnectionFactoryManager createConnectionFactoryManager() {
        Configuration configuration = getSkifConfiguration();
        Map<Object, ConnectionFactory> factoryMap = new HashMap<Object, ConnectionFactory>(2);
        factoryMap.put(SnapshotVersion.CURRENT, createJDBCConnectionFactory(configuration));
        factoryMap.put(SnapshotVersion.OLD, createJDBCConnectionFactory(configuration));
        return new ConnectionFactoryManagerMultiVersionImpl(factoryMap);
    }
}