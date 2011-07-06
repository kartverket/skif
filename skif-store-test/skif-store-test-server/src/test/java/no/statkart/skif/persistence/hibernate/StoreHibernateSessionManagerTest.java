package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.StoreSession;
import no.statkart.skif.store.persistence.hibernate.StoreHibernateSession;
import no.statkart.skif.store.persistence.hibernate.StoreHibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.StoreHibernateSessionFactoryManager;
import no.statkart.skif.store.persistence.hibernate.StoreHibernateSessionManager;
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
    StoreHibernateSessionManager sessionManager;
    StoreHibernateSessionFactoryManager hibernateSessionFactoryManager;

    private StoreHibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        Configuration cfg = new PropertiesConfiguration(getClass().getResource("/no/statkart/skif/storetest/config/persistence/skiftest-hibernate-singlevm.properties"));
        Properties properties = ConfigurationConverter.getProperties(cfg);
        return new StoreHibernateSessionFactoryBuilder(properties, "no/statkart/skif/storetest/persistence/hibernate");
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        StoreHibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestBubble.class);

        hibernateSessionFactoryManager = new StoreHibernateSessionFactoryManager(sfbuilder);
        sessionManager = new StoreHibernateSessionManager(hibernateSessionFactoryManager);

    }

    /**
     * Test uthenting og lukking av Hibernate Session via session manager.
     */
    public void testUthentingOgLukking() {
        StoreHibernateSession storeSession = sessionManager.getSession(ReplicaVersion.CURRENT);
        Session s = storeSession.getHibernateSession();

        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        assertNotNull(e);
        assertTrue(sessionManager.hasActiveSession((ReplicaVersion.CURRENT)));
        assertFalse(sessionManager.hasActiveSession((ReplicaVersion.OLD)));

        sessionManager.closeSession(ReplicaVersion.CURRENT);
        assertFalse(sessionManager.hasActiveSession((ReplicaVersion.CURRENT)));

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
        StoreSession wrapper = sessionManager.getSession(ReplicaVersion.CURRENT);
        StoreSession wrapper2 = sessionManager.getSession(ReplicaVersion.CURRENT);
        StoreSession wrapperOld = sessionManager.getSession(ReplicaVersion.OLD);
        StoreSession wrapperOld2 = sessionManager.getSession(ReplicaVersion.OLD);
        assertSame(wrapper, wrapper2);
        assertSame(wrapperOld, wrapperOld2);
        assertNotSame(wrapper, wrapperOld);
    }


    /**
     * Session instanser fra forskjellige managers skal være forskjellige
     */
    public void testInstancesFraForskejlligeMannagers() {
        StoreHibernateSessionManager sessionManager2 = new StoreHibernateSessionManager(hibernateSessionFactoryManager);
        StoreSession wrapper = sessionManager.getSession(ReplicaVersion.CURRENT);
        StoreSession wrapper2 = sessionManager2.getSession(ReplicaVersion.CURRENT);
        assertNotSame(wrapper, wrapper2);
    }
}