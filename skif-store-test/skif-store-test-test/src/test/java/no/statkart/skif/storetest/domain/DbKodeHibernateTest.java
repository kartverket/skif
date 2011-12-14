package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.kodeliste.DbKode;
import no.statkart.skif.store.kodeliste.DbKodeId;
import no.statkart.skif.store.kodeliste.DbKodeliste;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateVersionFactory;
import no.statkart.skif.store.persistence.hibernate.StoreHibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.type.EnumKodeIdType;
import no.statkart.skif.store.persistence.kodeliste.DbKodelisteLoader;
import no.statkart.skif.store.persistence.kodeliste.KodelisteManager;
import no.statkart.skif.store.persistence.kodeliste.KodelistePersister;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.demo.Baz;
import no.statkart.skif.storetest.domain.demo.BazId;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteLongId;
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
 * @since 2.0
 */
@Test
public class DbKodeHibernateTest {

    private SessionFactory setupHibernate() {
        StoreHibernateSessionFactoryBuilder sfbuilder = TestHelper.createStoreHibernateSessionFactoryBuilder();
        sfbuilder.addResource(EnumKodeIdType.class);
        sfbuilder.addResource(ADbKode.class);
        sfbuilder.addResource(BDbKode.class);
        sfbuilder.addResource(XStrDbKode.class);
        sfbuilder.addResourceWithSubclasses(CDbKode.class, C1DbKode.class, C2DbKode.class);
        sfbuilder.addResource(StoreTestDbKodelisteLong.class);
        sfbuilder.addResource(Foo.class);
        sfbuilder.addResource(Baz.class);
        SessionFactory sf = sfbuilder.build(new SnapshotVersionSeed(SnapshotVersion.CURRENT));
        assertNotNull(sf);
        return sf;
    }

    public void testLoadTestADbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        ADbKode obj = (ADbKode) session.load(ADbKode.class, new  ADbKodeId(new Long(1), SnapshotVersion.CURRENT));
        Assert.assertEquals(obj.getId(), ADbKodeId.A1Id);
        Class valueType = obj.getId().getValueType();
        Assert.assertEquals(valueType, Long.class);

    }


    public void testLoadTestBDbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        BDbKode obj = (BDbKode) session.load(BDbKode.class, new  BDbKodeId(new Long(1), SnapshotVersion.CURRENT));
        Assert.assertEquals(obj.getId(), BDbKodeId.B1Id);
    }

    public void testLoadTestCKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        CDbKode obj = (CDbKode) session.load(CDbKode.class, new C2DbKodeId(new Long(1), SnapshotVersion.CURRENT));
        Assert.assertEquals(obj.getId(), C1DbKodeId.C1AId);
    }

    public void testLoadTestC1DbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        C1DbKode obj = (C1DbKode) session.load(C1DbKode.class, new C1DbKodeId(new Long(1), SnapshotVersion.CURRENT));
        Assert.assertEquals(obj.getId(), C1DbKodeId.C1AId);
    }

    public void testLoadTestC2DbKode() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        C2DbKode obj = (C2DbKode) session.load(C2DbKode.class, new C2DbKodeId(new Long(10), SnapshotVersion.CURRENT));
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

    public void testLoadTestXStrDbKode() {
         SessionFactory sf = setupHibernate();
         Session session = sf.openSession();
         XStrDbKode obj = (XStrDbKode) session.load(XStrDbKode.class, XStrDbKodeId.AId);
         Assert.assertEquals(obj.getId(), XStrDbKodeId.AId);
     }

    public void testLastKodeliste() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        DbKodeliste dbKodeliste = (DbKodeliste) session.load(StoreTestDbKodelisteLong.class, new StoreTestDbKodelisteLongId(10001L, SnapshotVersion.CURRENT));
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
        Assert.assertNotNull(list);

    }

    public void testLoadTestBaz() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        Baz obj = (Baz) session.load(Baz.class, BubbleIds.createInstance(BazId.class, new Long(502), SnapshotVersion.CURRENT));
        Assert.assertEquals(obj.getTestAEnumKodeId(), AEnumKodeId.KodeAId);
        Assert.assertEquals(obj.getTestC2DbKodeId(), C2DbKodeId.C2BId);
    }

}
