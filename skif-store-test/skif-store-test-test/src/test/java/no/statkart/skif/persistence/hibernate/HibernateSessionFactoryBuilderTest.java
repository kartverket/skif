package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.storetest.domain.demo.TestEntity;
import org.hibernate.Query;
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
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new HibernateSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }

    public void testCreateFactorySessionAndConnection() throws SQLException, InterruptedException {
        HibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestEntity.class);
        SessionFactory sf = sfbuilder.build(new SnapshotVersionSeed(SnapshotVersion.CURRENT));
        assertNotNull(sf);
        Session s = sf.openSession();
        Connection c = s.connection();
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery("select 1 from dual");
        rs.next();
        assertEquals(rs.getInt(1), 1);
        statement.close();
        c.close();
        s.close();
        sf.close();
    }

    @Test(invocationCount = 1/*200*/)
    public void testCreateFactorySessionAndConnectionMulti() throws SQLException, InterruptedException {
        testCreateFactorySessionAndConnection();
    }

    public void testCreateFactoryWithEntity() throws SQLException {
        HibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestEntity.class);
        SessionFactory sf = sfbuilder.build(new SnapshotVersionSeed(SnapshotVersion.CURRENT));
        assertNotNull(sf);
        Session s = sf.openSession();
        Query query = s.createQuery("from TestEntity");
        List list = query.list();
        assertNotNull(list);
        s.close();
        sf.close();
    }

    @Test(invocationCount = 1/*200*/)
    public void testCreateFactoryWithEntityMulti() throws SQLException {
        testCreateFactoryWithEntity();
    }

}
