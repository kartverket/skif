package no.statkart.skif.storetest.service.nonhisttest;

import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.config.NonhistTestServerModule;
import no.statkart.skif.storetest.domain.nonhist.BarId;
import no.statkart.skif.storetest.domain.nonhist.Foo;
import no.statkart.skif.storetest.domain.nonhist.FooId;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.hibernate.*;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tester historisk navigering mellom bobler via servicekall og store
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @author Steinar Hansen
 */
@Test
public class NonhistTestServiceTest extends SkifTestCase {

    public NonhistTestServiceTest() {
        setModuleClass(NonhistTestServiceClientModule.class);
        setSingleVmServerModuleClass(NonhistTestServerModule.class);
    }

    private SessionFactory setupHibernate() {
        HibernateSessionFactoryBuilder sfbuilder = TestHelper.createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(no.statkart.skif.storetest.domain.nonhist.Foo.class);
        sfbuilder.addResource(no.statkart.skif.storetest.domain.demo.Foo.class);
        SessionFactory sf = sfbuilder.build();
        AssertJUnit.assertNotNull(sf);
        return sf;
    }

    public void testNormalHibernate() {
        try {
            SessionFactory sessionFactory = setupHibernate();

            Session session = sessionFactory.openSession();

            Query query = session.createQuery("from no.statkart.skif.storetest.domain.nonhist.Foo");
            List list = query.list();
            for (int i = 0; i < list.size(); i++) {
                Object o = list.get(i);
                System.out.println(o.toString());
            }

            SQLQuery sqlQuery = session.createSQLQuery("select HANSTE_HIST.snapshot_time.set_t(HANSTE_HIST.snapshot_time.to_t('9999-01-01 00:00:00.00')) from dual");
            sqlQuery.list();

            Query query2 = session.createQuery("from no.statkart.skif.storetest.domain.demo.Foo");
            List list2 = query2.list();
            for (int i = 0; i < list2.size(); i++) {
                Object o = list2.get(i);
                System.out.println(o.toString());
            }
        } catch (HibernateException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void testFindFoos() {
        NonhistTestService nonhistTestService = injector.getInstance(NonhistTestService.class);
        Set<BubbleObject> foos = nonhistTestService.findAllCurrentFoos();
        assertEquals(foos.size(), 4);
        boolean gotDemoDomainObject = false;
        boolean gotNonhistDomainObject = false;
        for (Iterator<BubbleObject> iterator = foos.iterator(); iterator.hasNext(); ) {
            BubbleObject next = iterator.next();            
            if(next.getClass().getPackage().getName().contains("domain.demo")){
                gotDemoDomainObject = true;     
                no.statkart.skif.storetest.domain.demo.Foo foo = (no.statkart.skif.storetest.domain.demo.Foo) next;                
                Class type = foo.getId().getValueType();
                assertEquals(type.getSimpleName(), "Long");
            }else if(next.getClass().getPackage().getName().contains("domain.nonhist")){
                gotNonhistDomainObject = true;
                Foo foo = (Foo) next;
                Class type = foo.getId().getValueType();
                assertEquals(type.getSimpleName(), "Long");

            }
        }
        assertTrue(gotDemoDomainObject, "Fikk ikke returnert minst ett domain.demo objekt");
        assertTrue(gotNonhistDomainObject, "Fikk ikke returnert minst ett domain.nonhist objekt");
    }

    /**
     * public void testFindFooIdsForNr() {
     * <p/>
     * Set<FooId<Foo>> fooIds = histTestService.findFooIdsForNr(2200);
     * assertEquals(fooIds.size(), 1);
     * <p/>
     * <p/>
     * }
     * <p/>
     * /**
     *
     * @since 2.1
     */
    public void testFindBarIdsAliveAtSnapshot() {
        Set<BarId<?>> barIds = new HashSet<BarId<?>>();
        barIds.add(new BarId(1001L));
        barIds.add(new BarId(1002L));

        NonhistTestService nonhistTestService = injector.getInstance(NonhistTestService.class);
        List<BarId> barIdsAliveAtSnapshot1 = nonhistTestService.findBarIdsAliveAtSnapshot(barIds, SnapshotVersion.CURRENT);
        assertEquals(barIdsAliveAtSnapshot1, barIds, "Fant ikke riktig current barIds");

        SnapshotVersion oldSnapshot = SnapshotVersion.createInstance("2011-10-02 08:00:00.00");
        List<BarId> barIdsAliveAtSnapshot2 = nonhistTestService.findBarIdsAliveAtSnapshot(barIds, oldSnapshot);
        assertTrue(barIdsAliveAtSnapshot2.isEmpty(), "Fant barIds når ingen skulle ha vært der");
    }

    /**
     * @since 2.1
     */
    public void testFindBarIdsForFooIds() {
        Set<FooId<?>> fooIds = new HashSet<FooId<?>>();
        fooIds.add(new FooId<Foo>(100L));

        NonhistTestService nonhistTestService = injector.getInstance(NonhistTestService.class);
        SnapshotVersion snapshotVersion1 = SnapshotVersion.createInstance("2011-10-02 08:00:00.00");
        Map<FooId<?>, Set<BarId<?>>> barIdsForFooIdsSnapshot1 = nonhistTestService.findBarIdsForFooIds(fooIds, snapshotVersion1);
        assertTrue(barIdsForFooIdsSnapshot1.isEmpty(), "Fikk historiske barIds som ikke skulle ha eksistert da");

        SnapshotVersion snapshotVersion2 = SnapshotVersion.createInstance("2011-10-02 08:03:00.00");
        Map<FooId<?>, Set<BarId<?>>> barIdsForFooIdsSnapshot2 = nonhistTestService.findBarIdsForFooIds(fooIds, snapshotVersion2);
        assertEquals(barIdsForFooIdsSnapshot2.size(), 1, "Fant feil antall fooIds");
        for (Map.Entry<FooId<?>, Set<BarId<?>>> entry : barIdsForFooIdsSnapshot2.entrySet()) {
            assertEquals(entry.getKey().getSnapshotVersion(), snapshotVersion2, "Feil snapshot på fooId");
            Set<BarId<?>> barIds = entry.getValue();
            assertEquals(barIds.size(), 2, "Fant feil antall barIds");
            for (BarId<?> barId : barIds) {
                assertEquals(barId.getSnapshotVersion(), snapshotVersion2, "Feil snapshot på barId");
            }
        }

        Map<FooId<?>, Set<BarId<?>>> barIdsForFooIdsCurrent = nonhistTestService.findBarIdsForFooIds(fooIds, SnapshotVersion.CURRENT);
        assertEquals(barIdsForFooIdsCurrent.size(), 1, "Fant feil antall fooIds");
        for (Map.Entry<FooId<?>, Set<BarId<?>>> entry : barIdsForFooIdsCurrent.entrySet()) {
            assertEquals(entry.getKey().getSnapshotVersion(), SnapshotVersion.CURRENT, "Feil snapshot på fooId");
            Set<BarId<?>> barIds = entry.getValue();
            assertEquals(barIds.size(), 2, "Fant feil antall barIds");
            for (BarId<?> barId : barIds) {
                assertEquals(barId.getSnapshotVersion(), SnapshotVersion.CURRENT, "Feil snapshot på barId");
            }
        }
    }
}
