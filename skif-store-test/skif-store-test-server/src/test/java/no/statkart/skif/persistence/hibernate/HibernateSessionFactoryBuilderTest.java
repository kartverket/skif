package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.storetest.domain.TestEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 */
@Test
public class HibernateSessionFactoryBuilderTest {

    private HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration(getClass().getResource("/no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties"));
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new HibernateSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }

    public void testCreateFactorySessionAndConnection() throws SQLException {
        HibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        SessionFactory sf = sfbuilder.build(ReplicaVersion.CURRENT);
        assertNotNull(sf);
        Session s = sf.openSession();
        Connection c = s.connection();
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery("select 1 from dual");
        rs.next();
        assertEquals(rs.getInt(1), 1);
    }


    public void testCreateFactoryWithEntity() throws SQLException {
        HibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestEntity.class);
        SessionFactory sf = sfbuilder.build(ReplicaVersion.CURRENT);
        assertNotNull(sf);
        Session s = sf.openSession();
        List list = s.createQuery("from TestEntity").list();
        assertNotNull(list);
    }

}
