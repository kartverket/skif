package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.hibernate.StoreHibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.StoreHibernateSessionFactoryManager;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreHibernateSessionFactoryManagerTest {
    private StoreHibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new StoreHibernateSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }

    public void test() {
        StoreHibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        StoreHibernateSessionFactoryManager hibernateSessionFactoryManager = new StoreHibernateSessionFactoryManager(sfbuilder);

        SessionFactory sf = hibernateSessionFactoryManager.getFactory(ReplicaVersion.CURRENT);
        assertNotNull(sf);
        Session s = sf.openSession();

        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
    }

    public void testOldReplicaVersion() {
        StoreHibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);
        StoreHibernateSessionFactoryManager hibernateSessionFactoryManager = new StoreHibernateSessionFactoryManager(sfbuilder);

        // Opprett objekt
        SessionFactory sf = hibernateSessionFactoryManager.getFactory(ReplicaVersion.CURRENT);
        assertNotNull(sf);
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        TestBubble e1 = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        e1.setText("Test");
        t.commit();

        // Start ny transaksjon og begynn å endre objekt
        t = s.beginTransaction();
        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        e.setText("Test 2");
        s.save(e);

        // Hent ut endret objekt via ReplicaVersion.OLD. Siden det er en annen session vil ikke uncommittet verdier
        // ble lest.
        SessionFactory sfOld = hibernateSessionFactoryManager.getFactory(ReplicaVersion.OLD);
        assertNotNull(sfOld);
        Session sOld = sfOld.openSession();
        TestBubble eOld = (TestBubble) sOld.get(TestBubble.class, new TestBubbleId(1));
        assertEquals("Test", eOld.getText());
        sOld.close();
        t.commit();

        sOld = sfOld.openSession();
        eOld = (TestBubble) sOld.get(TestBubble.class, new TestBubbleId(1));
        assertEquals("Test 2", eOld.getText());
    }
}