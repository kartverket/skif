package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.kodelistesupport.DbKode;
import no.statkart.skif.store.kodelistesupport.DbKodeId;
import no.statkart.skif.store.kodelistesupport.DbKodeliste;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateVersionFactory;
import no.statkart.skif.store.persistence.hibernate.StoreHibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.kodeliste.DbKodelisteLoader;
import no.statkart.skif.store.persistence.kodeliste.KodelisteManager;
import no.statkart.skif.store.persistence.kodeliste.KodelistePersister;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteId;
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
public class DbKodeHibernateTest {

    private SessionFactory setupHibernate() {
        StoreHibernateSessionFactoryBuilder sfbuilder = TestHelper.createStoreHibernateSessionFactoryBuilder();
        sfbuilder.addResourceUsingRelativePath("kodeliste", ADbKode.class);
        sfbuilder.addResourceUsingRelativePath("kodeliste", BDbKode.class);
        sfbuilder.addResourceWithSubclassesUsingRelativePath("kodeliste", CDbKode.class, C1DbKode.class, C2DbKode.class);
        sfbuilder.addResourceUsingRelativePath("kodeliste", StoreTestDbKodeliste.class);
        SessionFactory sf = sfbuilder.build(new SnapshotVersionSeed(SnapshotVersion.CURRENT));
        assertNotNull(sf);
        return sf;
    }

    public void testLoadTestADbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        ADbKode obj = (ADbKode) session.load(ADbKode.class, ADbKodeId.createInstance(1));
        Assert.assertEquals(obj.getId(), ADbKodeId.A1Id);
    }


    public void testLoadTestBDbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        BDbKode obj = (BDbKode) session.load(BDbKode.class, BDbKodeId.createInstance(1));
        Assert.assertEquals(obj.getId(), BDbKodeId.B1Id);
    }

    public void testLoadTestCKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        CDbKode obj = (CDbKode) session.load(CDbKode.class, C2DbKodeId.createInstance(1));
        Assert.assertEquals(obj.getId(), C1DbKodeId.C1AId);
    }

    public void testLoadTestC1DbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        C1DbKode obj = (C1DbKode) session.load(C1DbKode.class, C1DbKodeId.createInstance(1));
        Assert.assertEquals(obj.getId(), C1DbKodeId.C1AId);
    }

    public void testLoadTestC2DbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        C2DbKode obj = (C2DbKode) session.load(C2DbKode.class, C2DbKodeId.createInstance(10));
        Assert.assertEquals(obj.getId(), C2DbKodeId.C2A1Id);
    }

    public void testLoadAlleTestC2DbKoder() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        List list = session.createCriteria(C2DbKode.class).list();
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(list.get(0).getClass(), C2DbKode.class);
        Assert.assertEquals(list.get(1).getClass(), C2DbKode.class);
    }

    public void testLoadAlleTestCDbKoder() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        List list = session.createCriteria(CDbKode.class).list();
        Assert.assertEquals(list.size(), 4);

        List list2 = session.createCriteria(CDbKode.class).list();
        for (int i = 0; i < list.size(); i++) {
            Assert.assertSame(list.get(i), list2.get(i));
        }
    }

    public void testLastKodeliste() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        DbKodeliste dbKodeliste = (DbKodeliste) session.load(StoreTestDbKodeliste.class, new StoreTestDbKodelisteId(10001L, SnapshotVersion.CURRENT));
        Assert.assertNotNull(dbKodeliste);
    }

    public void testLastDbKodelisterOgKoder() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();

        DbKodelisteLoader kodelisteLoader = new DbKodelisteLoader() {
            @Override
            public List<DbKodeliste> load(Session session, Map<DbKodeId<?>, DbKode> kodeMap) {
                return load(session, DbKodeliste.class, kodeMap);
            }
        };

        Map<DbKodeId<?>, DbKode> kodeMap = new HashMap<DbKodeId<?>, DbKode>();
        List<DbKodeliste> kodelister = kodelisteLoader.load(session, kodeMap);
        Assert.assertNotNull(kodelister);
    }


    public void testKodelisteManager() {
        SessionFactory sf = setupHibernate();
        // TODO: Fix dette er feil.
        HibernateStoreSession wrapper = HibernateVersionFactory.Accessor.get().createHibernateStoreSession(sf.openSession(), new SnapshotVersionSeed(SnapshotVersion.CURRENT));
        KodelisteManager kodelisteManager = new KodelisteManager();



        DbKodelisteLoader kodelisteLoader = new DbKodelisteLoader() {
            @Override
            public List<DbKodeliste> load(Session session, Map<DbKodeId<?>, DbKode> kodeMap) {
                return load(session, DbKodeliste.class, kodeMap);
            }
        };

        KodelistePersister kodelistePersister = new KodelistePersister(wrapper, kodelisteLoader, kodelisteManager);
        Collection<? extends BubbleObject> list = kodelistePersister.getAllKodelisterAndKoder();
        Collection<? extends BubbleObject> list2 = kodelistePersister.getAllKodelisterAndKoder();
        Assert.assertNotNull(list);
    }
}
