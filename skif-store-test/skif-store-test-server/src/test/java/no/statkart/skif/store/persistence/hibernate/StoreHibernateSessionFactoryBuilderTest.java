package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionHolder;
import no.statkart.skif.storetest.domain.TestBubble;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreHibernateSessionFactoryBuilderTest {


    private HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new StoreHibernateSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }

    public void testCreateFactoryWithEntity() throws SQLException {
        HibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        SessionFactory sf = sfbuilder.build(new SnapshotVersionHolder(SnapshotVersion.CURRENT));
        assertNotNull(sf);
        Session s = sf.openSession();
        List list = s.createQuery("from TestBubble").list();
        assertNotNull(list);
        assertTrue(list.size()>0);
        TestBubble b = (TestBubble) list.get(0);
        assertEquals(b.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);
    }

    public void testCreateFactoryWithEntity_OLD() throws SQLException {
        HibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        SessionFactory sf = sfbuilder.build(new SnapshotVersionHolder(SnapshotVersion.OLD));
        assertNotNull(sf);
        Session s = sf.openSession();
        List list = s.createQuery("from TestBubble").list();
        assertNotNull(list);
        assertTrue(list.size()>0);
        TestBubble b = (TestBubble) list.get(0);
        assertEquals(b.getId().getSnapshotVersion(), SnapshotVersion.OLD);
    }
}
