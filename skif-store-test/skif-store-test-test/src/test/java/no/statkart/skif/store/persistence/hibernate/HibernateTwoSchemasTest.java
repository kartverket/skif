package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Injector;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import no.statkart.skif.storetest.domain.demo.TestEntity;
import no.statkart.skif.storetest.history.TestHistoricBubble;
import no.statkart.skif.storetest.history.TestHistoricBubbleId;
import no.statkart.skif.storetest.history.TestHistoricEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Steinar Hansen
 */
@Test
public class HibernateTwoSchemasTest {

    private SessionFactory setupHibernate() {
        HibernateSessionFactoryBuilder sfbuilder = TestHelper.createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestEntity.class);
        sfbuilder.addResource(TestBubble.class);
        sfbuilder.addResource(TestHistoricEntity.class);
        sfbuilder.addResource(TestHistoricBubble.class);
        sfbuilder.addResourceUsingAbsolutePath(no.statkart.skif.storetest.domain.nonhist.Foo.class, "no/statkart/skif/storetest/persistence/hibernate/Foo2.hbm.xml");
        sfbuilder.addResource(no.statkart.skif.storetest.domain.demo.Foo.class);
        SessionFactory sf = sfbuilder.build(new SnapshotVersionSeed(SnapshotVersion.CURRENT));
        AssertJUnit.assertNotNull(sf);
        return sf;
    }

    public void testHibernateLoadFraToForskjelligeSkjemaer() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestEntity te = (TestEntity) session.load(TestEntity.class, 1l);
        TestHistoricEntity the = (TestHistoricEntity) session.load(TestHistoricEntity.class, 1l);
        assertNotNull(te.getText());
        assertNotNull(the.getText());
    }

    public void testHibernateOppdaterIToForskjelligeSkjemaer() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestEntity te = (TestEntity) session.load(TestEntity.class, 1l);
        te.setText("NyTekst");
        TestHistoricEntity the = (TestHistoricEntity) session.load(TestHistoricEntity.class, 1l);
        the.setText("NyTekst");
        session.flush();
        assertEquals(te.getText(), the.getText());
    }

    public void testHibernateInsertIToForskjelligeSkjemaer() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestEntity te = (TestEntity) session.load(TestEntity.class, 1l);
        TestEntity te2 = new TestEntity(2l, "2Tekst");
        TestHistoricEntity the = (TestHistoricEntity) session.load(TestHistoricEntity.class, 1l);
        TestHistoricEntity the2 = new TestHistoricEntity(2l, "2Tekst");
        session.save(te2);
        session.save(the2);
        session.flush();
        sf.close();
        assertEquals(te2.getText(), the2.getText());
    }

    public void testHibernateDeleteIToForskjelligeSkjemaer() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestEntity te2 = null;
        try {
            te2 = (TestEntity) session.load(TestEntity.class, 2l);
            session.delete(te2);
        } catch (org.hibernate.ObjectNotFoundException e) {
            //Ignorer, allerede slettet
        }
        TestHistoricEntity the2 = null;
        try {
            the2 = (TestHistoricEntity) session.load(TestHistoricEntity.class, 2l);
            session.delete(the2);
        } catch (org.hibernate.ObjectNotFoundException e) {
            //Ignorer, allerede slettet
        }
        session.flush();
        sf.close();
//        assertEquals(te2.getText(), the2.getText());
    }

    @Test(enabled=false)
    public void testStoreGet() {
        Injector injector = TestHelper.createInjectorStoreSnapshotVersionImpl();
        Store store = injector.getInstance(Store.class);
        TestBubble tb = store.get(new TestBubbleId<TestBubble>(1));
        assertNotNull(tb);
        TestHistoricBubble thb = store.get(new TestHistoricBubbleId<TestHistoricBubble>(1));
        assertNotNull(thb);
        System.out.println(thb.toString());
        System.out.println("TestHistoricBubble.getText(): " + thb.getText());
    }

    public void testCriteriaSearchInHistoricNonBubble() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        List<TestHistoricEntity> thbs = session.createCriteria(TestHistoricEntity.class).list();
        assertTrue(thbs.size() >= 1);
        for (int i = 0; i < thbs.size(); i++) {
            TestHistoricEntity testHistoricEntity = thbs.get(i);
            System.out.println(testHistoricEntity);
        }
    }

    public void testCriteriaSearchInHistoricBubble() {
        Injector injector = TestHelper.createInjectorStoreSnapshotVersionImpl();
        ServiceRequestScope scope = TestHelper.enterServer(injector);
        HibernateStoreSessionManager storeSessionManager = injector.getInstance(HibernateStoreSessionManager.class);
        String t1 = "2011-10-01 08:00:00.00";
        SnapshotVersion sv1 = SnapshotVersion.createInstance(t1);
        HibernateStoreSession s1 = storeSessionManager.acquireSnapshotStoreSession(sv1);
        Session wrappedSession = s1.getWrappedSession();
        List<TestHistoricBubble> thbs = wrappedSession.createCriteria(TestHistoricBubble.class).list();
        assertTrue(thbs.size() == 1);
        for (int i = 0; i < thbs.size(); i++) {
            TestHistoricBubble testHistoricBubble = thbs.get(i);
            System.out.println(testHistoricBubble.getText());
        }
        storeSessionManager.releaseSnapshotStoreSession(s1);
        storeSessionManager.close();
        TestHelper.exitServer(scope);
//        injector.getInstance(HibernateSessionFactoryManager.class).close();
    }

    public void testSqlSearchInBubble() {
        Injector injector = TestHelper.createInjectorStoreSnapshotVersionImpl();
        ServiceRequestScope scope = TestHelper.enterServer(injector);
        HibernateStoreSessionManager storeSessionManager = injector.getInstance(HibernateStoreSessionManager.class);
        String t1 = "2011-10-01 08:00:00.00";
        SnapshotVersion sv1 = SnapshotVersion.createInstance(t1);
        HibernateStoreSession s1 = storeSessionManager.acquireSnapshotStoreSession(sv1);
        Session wrappedSession = s1.getWrappedSession();
        TestHistoricBubble thb = (TestHistoricBubble) wrappedSession.createQuery("from TestHistoricBubble t where t.id=1").uniqueResult();
        assertNotNull(thb);
        System.out.println(thb.getText());
        List<TestHistoricBubble> thbs = wrappedSession.createQuery("from TestHistoricBubble").list();
        assertTrue(thbs.size() >= 1);
        for (int i = 0; i < thbs.size(); i++) {
            TestHistoricBubble testBubble = thbs.get(i);
            System.out.println(testBubble);
        }
        storeSessionManager.releaseSnapshotStoreSession(s1);
        storeSessionManager.close();
        TestHelper.exitServer(scope);
        injector.getInstance(HibernateSessionFactoryManager.class).close();
    }

    public void testProgrammaticSchemaConfig() {
        Injector injector = TestHelper.createInjectorStoreSnapshotVersionImpl();
        ServiceRequestScope scope = TestHelper.enterServer(injector);
        HibernateStoreSessionManager storeSessionManager = injector.getInstance(HibernateStoreSessionManager.class);
        String t1 = "2011-10-01 08:00:00.00";
        SnapshotVersion sv1 = SnapshotVersion.createInstance(t1);
        HibernateStoreSession s1 = storeSessionManager.acquireSnapshotStoreSession(sv1);
        Session wrappedSession = s1.getWrappedSession();

//        wrappedSession.get(TestHistoricBubble.class, ).getClassMetadata()
        TestHistoricBubble thb = (TestHistoricBubble) wrappedSession.createQuery("from TestHistoricBubble t where t.id=1").uniqueResult();
        assertNotNull(thb);
        System.out.println(thb.getText());
        List<TestHistoricBubble> thbs = wrappedSession.createQuery("from TestHistoricBubble").list();
        assertTrue(thbs.size() >= 1);
        for (int i = 0; i < thbs.size(); i++) {
            TestHistoricBubble testBubble = thbs.get(i);
            System.out.println(testBubble);
        }
        storeSessionManager.releaseSnapshotStoreSession(s1);
        storeSessionManager.close();
        TestHelper.exitServer(scope);
        injector.getInstance(HibernateSessionFactoryManager.class).close();
    }

    public void testHibernateLoadFraToForskjelligeSkjemaerV2() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        no.statkart.skif.storetest.history.Foo fooh = (no.statkart.skif.storetest.history.Foo) session.load(no.statkart.skif.storetest.history.Foo.class, 1l);
        no.statkart.skif.storetest.domain.demo.Foo foo = (no.statkart.skif.storetest.domain.demo.Foo) session.load(no.statkart.skif.storetest.domain.demo.Foo.class, 1l);
        assertNotNull(foo.getNavn());
        assertNotNull(fooh.getNavn());
    }

}
