package no.statkart.skif.storetest.domain;

import no.statkart.skif.ConfigurationConverter;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.kodelistesupport.*;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.kodeliste.BubbleKodelisteManager;
import no.statkart.skif.store.persistence.kodeliste.BubbleKodelistePersister;
import no.statkart.skif.store.persistence.kodeliste.DbBubbleKodelisteLoader;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.kodeliste.*;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodelisteId;
import no.statkart.skif.util.ResourceUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.util.*;

import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
@Test
public class DbKodeHibernateTest {

    private SessionFactory setupHibernate() {
        HibernateSessionFactoryBuilder sfbuilder = TestHelper.createStoreHibernateSessionFactoryBuilder();
        sfbuilder.addResourceUsingRelativePath("kodeliste", TestADbKode.class);
        sfbuilder.addResourceUsingRelativePath("kodeliste", TestBDbKode.class);
        sfbuilder.addResourceWithSubclassesUsingRelativePath("kodeliste", TestCDbKode.class, TestC1DbKode.class, TestC2DbKode.class);
        sfbuilder.addResourceUsingRelativePath("kodeliste", DbKodeliste.class);
        SessionFactory sf = sfbuilder.build(ReplicaVersion.CURRENT);
        assertNotNull(sf);
        return sf;
    }

    public void testLoadTestADbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestADbKode obj = (TestADbKode) session.load(TestADbKode.class, TestADbKodeId.createInstance(1));
        Assert.assertEquals(obj.getId(), TestADbKodeId.A1Id);
    }


    public void testLoadTestBDbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestBDbKode obj = (TestBDbKode) session.load(TestBDbKode.class, TestBDbKodeId.createInstance(1));
        Assert.assertEquals(obj.getId(), TestBDbKodeId.B1Id);
    }

    public void testLoadTestCKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestCDbKode obj = (TestCDbKode) session.load(TestCDbKode.class, TestC2DbKodeId.createInstance(1));
        Assert.assertEquals(obj.getId(), TestC1DbKodeId.C1AId);
    }

    public void testLoadTestC1DbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestC1DbKode obj = (TestC1DbKode) session.load(TestC1DbKode.class, TestC1DbKodeId.createInstance(1));
        Assert.assertEquals(obj.getId(), TestC1DbKodeId.C1AId);
    }

    public void testLoadTestC2DbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestC2DbKode obj = (TestC2DbKode) session.load(TestC2DbKode.class, TestC2DbKodeId.createInstance(10));
        Assert.assertEquals(obj.getId(), TestC2DbKodeId.C2A1Id);
    }

    public void testLoadAlleTestC2DbKoder() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        List list = session.createCriteria(TestC2DbKode.class).list();
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(list.get(0).getClass(), TestC2DbKode.class);
        Assert.assertEquals(list.get(1).getClass(), TestC2DbKode.class);
    }

    public void testLoadAlleTestCDbKoder() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        List list = session.createCriteria(TestCDbKode.class).list();
        Assert.assertEquals(list.size(), 4);

        List list2 = session.createCriteria(TestCDbKode.class).list();
        for (int i = 0; i < list.size(); i++) {
            Assert.assertSame(list.get(i), list2.get(i));
        }
    }

    public void testLastKodeliste() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        DbKodeliste dbKodeliste = (DbKodeliste) session.load(DbKodeliste.class, new DbKodelisteId(10001));
        Assert.assertNotNull(dbKodeliste);
    }

    public void testLastDbKodelisterOgKoder() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();

        DbBubbleKodelisteLoader kodelisteLoader = new DbBubbleKodelisteLoader() {
            @Override
            public List<DbBubbleKodeliste> load(Session session, Map<DbBubbleKodeId<?>, DbBubbleKode> kodeMap) {
                return load(session, DbKodeliste.class, kodeMap);
            }
        };

        Map<DbBubbleKodeId<?>, DbBubbleKode> kodeMap = new HashMap<DbBubbleKodeId<?>, DbBubbleKode>();
        List<DbBubbleKodeliste> kodelister = kodelisteLoader.load(session, kodeMap);
        Assert.assertNotNull(kodelister);
    }


    public void testKodelisteManager() {
        SessionFactory sf = setupHibernate();
        HibernateStoreSession wrapper = new HibernateStoreSession(sf.openSession(), ReplicaVersion.CURRENT);
        BubbleKodelisteManager kodelisteManager = new BubbleKodelisteManager();

        DbBubbleKodelisteLoader kodelisteLoader = new DbBubbleKodelisteLoader() {
            @Override
            public List<DbBubbleKodeliste> load(Session session, Map<DbBubbleKodeId<?>, DbBubbleKode> kodeMap) {
                return load(session, DbKodeliste.class, kodeMap);
            }
        };

        BubbleKodelistePersister kodelistePersister = new BubbleKodelistePersister(wrapper, kodelisteLoader, kodelisteManager);
        Collection<? extends BubbleObject> list = kodelistePersister.getAllKodelisterAndKoder();
        Collection<? extends BubbleObject> list2 = kodelistePersister.getAllKodelisterAndKoder();
        Assert.assertNotNull(list);
    }
}
