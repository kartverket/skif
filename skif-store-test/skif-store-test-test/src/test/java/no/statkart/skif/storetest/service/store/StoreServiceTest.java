package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import com.google.inject.Key;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.*;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreServiceTest extends StoreTestTestCase {

    @Inject
    private StoreService storeService;

    public void testStoreService() {
        StoreService store = injector.getInstance(Key.get(StoreService.class));
        TestBubbleId<?> a1Id = new TestBubbleId<TestBubble>(1);
        List<TestBubbleId> ids = new ArrayList<TestBubbleId>();
        ids.add(a1Id);

        TestBubble bubble = store.getObject(a1Id);
        assertEquals(a1Id, bubble.getId());

        List<TestBubble> bubbles = store.getObjects(ids);
        assertEquals(1, bubbles.size());
        assertEquals(a1Id, bubbles.get(0).getId());
    }

    public void testStoreGetOld() {
        StoreService store = injector.getInstance(Key.get(StoreService.class));
        TestBubbleId<?> a1Id = new TestBubbleId<TestBubble>(1L, SnapshotVersion.OLD);

        TestBubble bubble = store.getObject(a1Id);
        assertEquals(a1Id, bubble.getId());
        assertEquals(bubble.getId().getSnapshotVersion(), SnapshotVersion.OLD);
    }

    @Test
    public void testStoreGetFoo() {

        StoreService store = injector.getInstance(Key.get(StoreService.class));

        Foo currentFoo = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.CURRENT));
        assertNotNull(currentFoo);
        Assert.assertEquals(currentFoo.getNr(), 2200);
        Assert.assertEquals(currentFoo.getNavn(), "KARTVEIEN");

        Foo oldestFoo = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance("2011-10-02 08:00:30.00")));
        Assert.assertNotNull(oldestFoo);
        Assert.assertEquals(oldestFoo.getNr(), 2200);
        Assert.assertEquals(oldestFoo.getNavn(), "KARTGATA");

        Foo newerFoo = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance("2011-10-02 08:01:30.00")));
        Assert.assertNotNull(newerFoo);
        Assert.assertEquals(newerFoo.getNr(), 2200);
        Assert.assertEquals(newerFoo.getNavn(), "KARTVEGEN");

        Foo newerFoo2 = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance("2011-10-02 08:02:30.00")));
        Assert.assertNotNull(newerFoo2);
        Assert.assertEquals(newerFoo2.getNr(), 2200);
        Assert.assertEquals(newerFoo2.getNavn(), "KARTVEIEN");

        Foo newerFoo3 = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance("2011-10-02 08:03:30.00")));
        Assert.assertNotNull(newerFoo3);
        Assert.assertEquals(newerFoo3.getNr(), 2200);
        Assert.assertEquals(newerFoo3.getNavn(), "KART-VEIEN");

        Foo newerFoo4 = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance("2011-10-02 08:04:30.00")));
        Assert.assertNotNull(newerFoo4);
        Assert.assertEquals(newerFoo4.getNr(), 2200);
        Assert.assertEquals(newerFoo4.getNavn(), "KARTVEIEN");

        Assert.assertNotSame(newerFoo4, currentFoo); //Samme objekt men forskjellig snapshotversion!
    }

    /**
     * Henter ut flere Foo med forskjellige snapshotversions i samme kall
     */
    @Test
    public void testStoreGetFooFlereSnapshotVersions() {
        StoreService store = injector.getInstance(StoreService.class);

        ArrayList<FooId<Foo>> ids = new ArrayList<FooId<Foo>>();
        ids.add(new FooId<Foo>(100L, SnapshotVersion.CURRENT));
        ids.add(new FooId<Foo>(100L, SnapshotVersion.createInstance("2011-10-02 08:00:30.00")));
        ids.add(new FooId<Foo>(100L, SnapshotVersion.createInstance("2011-10-02 08:01:30.00")));
        ids.add(new FooId<Foo>(100L, SnapshotVersion.createInstance("2011-10-02 08:06:30.00")));
        List<Foo> foos = store.getObjects(ids);

        Assert.assertEquals(foos.size(), 4);

    }

    @Test
    public void testStoreGetBar() {
        StoreService store = injector.getInstance(StoreService.class);
        Bar bar = store.getObject(new BarId<Bar>(1001L));

        Assert.assertEquals(bar.getHusnr(), 106);
        Assert.assertEquals(bar.getBokstav(), null);
        Assert.assertEquals(bar.getFooId().getValue(), new Long(100));
        Assert.assertEquals(bar.getId().getSnapshotVersion(), SnapshotVersion.CURRENT);
        Assert.assertEquals(bar.getFooId().getSnapshotVersion(), SnapshotVersion.CURRENT);

        Foo foo = store.getObject(bar.getFooId());
        assertEquals(foo.getNavn(), "KARTVEIEN");

        SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2011-10-02 08:03:15.00");
        Bar olderBar = store.getObject(new BarId<Bar>(1001L, snapshotVersion));
        Assert.assertEquals(olderBar.getHusnr(), 105);
        Assert.assertEquals(olderBar.getBokstav(), null);
        Assert.assertEquals(olderBar.getFooId().getValue(), new Long(100));
        Assert.assertEquals(olderBar.getId().getSnapshotVersion(), snapshotVersion);
        Assert.assertEquals(olderBar.getFooId().getSnapshotVersion(), snapshotVersion);

        Foo olderFoo = store.getObject(olderBar.getFooId());
        assertEquals(olderFoo.getNavn(), "KART-VEIEN");
    }

}
