package no.statkart.skif.store3.persistence.hibernate;


import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreInterceptor;
import no.statkart.skif.store.persistence.hibernate.HibernateVersionFactory;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Test av store3.HibernateSessionFactoyBuilder
 * @author Jan Holmen
 */
@Test
public class HibernateSessionFactoryBuilderTest {



    private no.statkart.skif.store3.persistence.hibernate.HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest/persistence/hibernate");
        return builder;
    }


    private Properties getHibernateProperties(){
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return properties;
    }



    public void testCreateFactoryWithEntity() throws SQLException {
        no.statkart.skif.store3.persistence.hibernate.HibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        SessionFactory sf = sfbuilder.build(new SnapshotVersionSeed(SnapshotVersion.CURRENT),getHibernateProperties(),null);
        assertNotNull(sf);
        Session s = sf.openSession();
        List list = s.createQuery("from TestBubble").list();
        assertNotNull(list);
        assertTrue(list.size()>0);
        TestBubble b = (TestBubble) list.get(0);
        assertEquals(b.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);
    }

    public void testCreateFactoryWithEntity_OLD() throws SQLException {
        no.statkart.skif.store3.persistence.hibernate.HibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        SessionFactory sf = sfbuilder.build(new SnapshotVersionSeed(SnapshotVersion.OLD),getHibernateProperties(),null);
        assertNotNull(sf);
        Session s = sf.openSession();
        List list = s.createQuery("from TestBubble").list();
        assertNotNull(list);
        assertTrue(list.size()>0);
        TestBubble b = (TestBubble) list.get(0);
        assertEquals(b.getId().getSnapshotVersion(), SnapshotVersion.OLD);
    }



}
