package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionHolder;
import no.statkart.skif.store2.BubbleObject2;
import no.statkart.skif.store2.kodelistesupport2.DbKode2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeId2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeliste2;
import no.statkart.skif.store2.persistence.hibernate.HibernateStoreSession2;
import no.statkart.skif.store2.persistence.hibernate.StoreHibernateSessionFactoryBuilder2;
import no.statkart.skif.store2.persistence.kodeliste.DbKodelisteLoader2;
import no.statkart.skif.store2.persistence.kodeliste.KodelisteManager2;
import no.statkart.skif.store2.persistence.kodeliste.KodelistePersister2;
import no.statkart.skif.storetest.domain2.kodeliste.*;
import no.statkart.skif.storetest2.TestHelper2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
@Test
public class DbKodeHibernateTest2 {

    private SessionFactory setupHibernate() {
        StoreHibernateSessionFactoryBuilder2 sfbuilder = TestHelper2.createStoreHibernateSessionFactoryBuilder();
        sfbuilder.addResourceUsingRelativePath("kodeliste", TestADbKode2.class);
        sfbuilder.addResourceUsingRelativePath("kodeliste", TestBDbKode2.class);
        sfbuilder.addResourceWithSubclassesUsingRelativePath("kodeliste", TestCDbKode2.class, TestC1DbKode2.class, TestC2DbKode2.class);
        sfbuilder.addResourceUsingRelativePath("kodeliste", TestDbKodelisteImpl2.class);
        SessionFactory sf = sfbuilder.build(new SnapshotVersionHolder(SnapshotVersion.CURRENT));
        assertNotNull(sf);
        return sf;
    }

    public void testLoadTestADbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestADbKode2 obj = (TestADbKode2) session.load(TestADbKode2.class, TestADbKodeId2.createInstance(1));
        Assert.assertEquals(obj.getId(), TestADbKodeId2.A1Id);
    }


    public void testLoadTestBDbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestBDbKode2 obj = (TestBDbKode2) session.load(TestBDbKode2.class, TestBDbKodeId2.createInstance(1));
        Assert.assertEquals(obj.getId(), TestBDbKodeId2.B1Id);
    }

    public void testLoadTestCKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestCDbKode2 obj = (TestCDbKode2) session.load(TestCDbKode2.class, TestC2DbKodeId2.createInstance(1));
        Assert.assertEquals(obj.getId(), TestC1DbKodeId2.C1AId);
    }

    public void testLoadTestC1DbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestC1DbKode2 obj = (TestC1DbKode2) session.load(TestC1DbKode2.class, TestC1DbKodeId2.createInstance(1));
        Assert.assertEquals(obj.getId(), TestC1DbKodeId2.C1AId);
    }

    public void testLoadTestC2DbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestC2DbKode2 obj = (TestC2DbKode2) session.load(TestC2DbKode2.class, TestC2DbKodeId2.createInstance(10));
        Assert.assertEquals(obj.getId(), TestC2DbKodeId2.C2A1Id);
    }

    public void testLoadAlleTestC2DbKoder() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        List list = session.createCriteria(TestC2DbKode2.class).list();
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(list.get(0).getClass(), TestC2DbKode2.class);
        Assert.assertEquals(list.get(1).getClass(), TestC2DbKode2.class);
    }

    public void testLoadAlleTestCDbKoder() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        List list = session.createCriteria(TestCDbKode2.class).list();
        Assert.assertEquals(list.size(), 4);

        List list2 = session.createCriteria(TestCDbKode2.class).list();
        for (int i = 0; i < list.size(); i++) {
            Assert.assertSame(list.get(i), list2.get(i));
        }
    }

    public void testLastKodeliste() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        DbKodeliste2 dbKodeliste = (DbKodeliste2) session.load(TestDbKodelisteImpl2.class, new TestDbKodelisteIdImpl2(10001L, SnapshotVersion.CURRENT));
        Assert.assertNotNull(dbKodeliste);
    }

    public void testLastDbKodelisterOgKoder() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();

        DbKodelisteLoader2 kodelisteLoader = new DbKodelisteLoader2() {
            @Override
            public List<DbKodeliste2> load(Session session, Map<DbKodeId2<?>, DbKode2> kodeMap) {
                return load(session, DbKodeliste2.class, kodeMap);
            }
        };

        Map<DbKodeId2<?>, DbKode2> kodeMap = new HashMap<DbKodeId2<?>, DbKode2>();
        List<DbKodeliste2> kodelister = kodelisteLoader.load(session, kodeMap);
        Assert.assertNotNull(kodelister);
    }


    public void testKodelisteManager() {
        SessionFactory sf = setupHibernate();
        HibernateStoreSession2 wrapper = new HibernateStoreSession2(sf.openSession(), SnapshotVersion.CURRENT);
        KodelisteManager2 kodelisteManager = new KodelisteManager2();

        DbKodelisteLoader2 kodelisteLoader = new DbKodelisteLoader2() {
            @Override
            public List<DbKodeliste2> load(Session session, Map<DbKodeId2<?>, DbKode2> kodeMap) {
                return load(session, DbKodeliste2.class, kodeMap);
            }
        };

        KodelistePersister2 kodelistePersister = new KodelistePersister2(wrapper, kodelisteLoader, kodelisteManager);
        Collection<? extends BubbleObject2> list = kodelistePersister.getAllKodelisterAndKoder();
        Collection<? extends BubbleObject2> list2 = kodelistePersister.getAllKodelisterAndKoder();
        Assert.assertNotNull(list);
    }
}
