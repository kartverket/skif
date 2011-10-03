package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import com.google.inject.Key;
import no.statkart.skif.SkifModule;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestGroup1Services;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.storetest.domain.*;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.storetest.wsapi.exception.impl.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

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
    public void testStoreGetFoo(){

        StoreService store = injector.getInstance(Key.get(StoreService.class));

        Foo currentFoo = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.CURRENT));
        Assert.assertNotNull(currentFoo);
        Assert.assertEquals(currentFoo.getA(), 21);
        Assert.assertEquals(currentFoo.getB(), "C1");

        Foo oldestFoo = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance(new Date(2011, 9, 2, 8, 0, 30).getTime())));
        Assert.assertNotNull(oldestFoo);
        Assert.assertEquals(oldestFoo.getA(), 10);
        Assert.assertEquals(oldestFoo.getB(), "A1");
        
        Foo newerFoo = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance(new Date(2011, 9, 2, 8, 1, 30).getTime())));
        Assert.assertNotNull(newerFoo);
        Assert.assertEquals(newerFoo.getA(), 11);
        Assert.assertEquals(newerFoo.getB(), "A1");
        
        Foo newerFoo2 = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance(new Date(2011, 9, 2, 8, 2, 30).getTime())));
        Assert.assertNotNull(newerFoo2);
        Assert.assertEquals(newerFoo2.getA(), 12);
        Assert.assertEquals(newerFoo2.getB(), "A3");

        Foo newerFoo3 = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance(new Date(2011, 9, 2, 8, 3, 30).getTime())));
        Assert.assertNotNull(newerFoo3);
        Assert.assertEquals(newerFoo3.getA(), 13);
        Assert.assertEquals(newerFoo3.getB(), "A1");

        Foo newerFoo4 = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance(new Date(2011, 9, 2, 8, 4, 30).getTime())));
        Assert.assertNotNull(newerFoo4);
        Assert.assertEquals(newerFoo4.getA(), 13);
        Assert.assertEquals(newerFoo4.getB(), "B52");

        Foo newerFoo5 = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance(new Date(2011, 9, 2, 8, 5, 30).getTime())));
        Assert.assertNotNull(newerFoo5);
        Assert.assertEquals(newerFoo5.getA(), 17);
        Assert.assertEquals(newerFoo5.getB(), "17");

        Foo newerFoo6 = store.getObject(new FooId<Foo>(new Long(100), SnapshotVersion.createInstance(new Date(2011, 9, 2, 8, 6, 30).getTime())));
        Assert.assertNotNull(newerFoo6);
        Assert.assertEquals(newerFoo6.getA(), 21);
        Assert.assertEquals(newerFoo6.getB(), "C1");

        Assert.assertNotSame(newerFoo6, currentFoo); //Samme objekt men forskjellig snapshotversion!
    }

    /**
     * Henter ut flere Foo med forskjellige snapshotversions i samme kall
     */
    @Test
    public void testStoreGetFooFlereSnapshotVersions(){
        StoreService store = injector.getInstance(StoreService.class);

        ArrayList<FooId<Foo>> ids = new ArrayList<FooId<Foo>>();
        ids.add(new FooId<Foo>(100L, SnapshotVersion.CURRENT));
        ids.add(new FooId<Foo>(100L, SnapshotVersion.createInstance(new Date(2011, 9, 2, 8, 0, 30).getTime())));
        ids.add(new FooId<Foo>(100L, SnapshotVersion.createInstance(new Date(2011, 9, 2, 8, 1, 30).getTime())));
        ids.add(new FooId<Foo>(100L, SnapshotVersion.createInstance(new Date(2011, 9, 2, 8, 6, 30).getTime())));
        List<Foo> foos = store.getObjects(ids);

        Assert.assertEquals(foos.size(), 4);

    }

}