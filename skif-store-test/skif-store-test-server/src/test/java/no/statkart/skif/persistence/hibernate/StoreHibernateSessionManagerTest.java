package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.StoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionFactoryManager;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionManagerOld;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.AssertJUnit.*;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreHibernateSessionManagerTest {
    HibernateStoreSessionManagerOld storeSessionManager;
    HibernateStoreSessionFactoryManager hibernateStoreSessionFactoryManager;

    private HibernateStoreSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration("no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties");
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new HibernateStoreSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        HibernateStoreSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);

        hibernateStoreSessionFactoryManager = new HibernateStoreSessionFactoryManager(sfbuilder);
        storeSessionManager = new HibernateStoreSessionManagerOld(hibernateStoreSessionFactoryManager);

    }

    /**
     * Test uthenting og lukking av Hibernate Session via session manager.
     */
    public void testUthentingOgLukking() {
        HibernateStoreSession storeSession = storeSessionManager.getSession(ReplicaVersion.CURRENT);
        Session s = storeSession.getWrappedSession();

        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
        assertTrue(storeSessionManager.hasActiveSession((ReplicaVersion.CURRENT)));
        assertFalse(storeSessionManager.hasActiveSession((ReplicaVersion.OLD)));

        storeSessionManager.closeSession(ReplicaVersion.CURRENT);
        assertFalse(storeSessionManager.hasActiveSession((ReplicaVersion.CURRENT)));

        try {
            TestBubble e1 = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        } catch (SessionException ex) {
            assertEquals("Session is closed!", ex.getMessage());
        }
    }

    /**
     * Multiple kall til getSession for samme session manager skal gi samme Session instans for gitt ReplicaVersion
     * verdi.
     */
    public void testInstancesFraSammeMannager() {
        StoreSession wrapper = storeSessionManager.getSession(ReplicaVersion.CURRENT);
        StoreSession wrapper2 = storeSessionManager.getSession(ReplicaVersion.CURRENT);
        StoreSession wrapperOld = storeSessionManager.getSession(ReplicaVersion.OLD);
        StoreSession wrapperOld2 = storeSessionManager.getSession(ReplicaVersion.OLD);
        assertSame(wrapper, wrapper2);
        assertSame(wrapperOld, wrapperOld2);
        assertNotSame(wrapper, wrapperOld);
    }


    /**
     * Session instanser fra forskjellige managers skal være forskjellige
     */
    public void testInstancesFraForskejlligeMannagers() {
        HibernateStoreSessionManagerOld storeSessionManager2 = new HibernateStoreSessionManagerOld(hibernateStoreSessionFactoryManager);
        StoreSession wrapper = storeSessionManager.getSession(ReplicaVersion.CURRENT);
        StoreSession wrapper2 = storeSessionManager2.getSession(ReplicaVersion.CURRENT);
        assertNotSame(wrapper, wrapper2);
    }
}