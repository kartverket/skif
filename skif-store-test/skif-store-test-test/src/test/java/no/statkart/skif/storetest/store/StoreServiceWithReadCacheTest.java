package no.statkart.skif.storetest.store;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.StoreClientReadCache;
import no.statkart.skif.store.StoreClientReadCacheImpl;
import no.statkart.skif.store.StoreServiceWithReadCache;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashMap;

import static no.statkart.skif.storetest.store.StoreServiceTestHelper.createStoreServiceWithNoObjects;
import static no.statkart.skif.storetest.store.StoreServiceTestHelper.createStoreServiceWithObjects;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

@Test
public class StoreServiceWithReadCacheTest {

    private StoreClientReadCache readCache;
    private Simple simple1;
    private Simple simple2;
    private Simple simple3;
    private Simple simple4;
    private StoreServiceWithReadCache storeServiceWithReadCache;

    @BeforeMethod
    void setup() {
        simple1 = new Simple(new SimpleId(1L), "Simple 1");
        simple2 = new Simple(new SimpleId(2L), "Simple 2");
        simple3 = new Simple(new SimpleId(3L), "Simple 3");
        simple4 = new Simple(new SimpleId(4L), "Simple 4");
        readCache = new StoreClientReadCacheImpl();
    }

    public void getObjectWhenPresentInReadCache() {
        readCache.put(simple1);
        storeServiceWithReadCache = new StoreServiceWithReadCache(createStoreServiceWithNoObjects(), readCache);
        Simple simple = storeServiceWithReadCache.getObject(simple1.getId());
        assertEquals(simple, simple1);
    }

    public void getObjectWhenNotPresentInCache() {
        storeServiceWithReadCache = new StoreServiceWithReadCache(createStoreServiceWithObjects(simple1), readCache);
        Simple simple = storeServiceWithReadCache.getObject(simple1.getId());
        assertEquals(simple, simple1);
    }

    public void getObjectsAllPresentInCache() {
        readCache.put(simple1);
        readCache.put(simple2);
        storeServiceWithReadCache = new StoreServiceWithReadCache(createStoreServiceWithNoObjects(), readCache);
        Collection<Simple> objectsFromCache = storeServiceWithReadCache.getObjects(ImmutableSet.of(simple1.getId(), simple2.getId()));
        assertFoundAllObjects(objectsFromCache, simple1, simple2);
    }

    public void getObjectsSomePresent() {
        readCache.put(simple1);
        readCache.put(simple2);
        storeServiceWithReadCache = new StoreServiceWithReadCache(createStoreServiceWithObjects(simple3, simple4), readCache);
        Collection<Simple> objectsFromCache = storeServiceWithReadCache.getObjects(ImmutableSet.of(simple1.getId(), simple2.getId(), simple3.getId(), simple4.getId()));
        assertFoundAllObjects(objectsFromCache, simple1, simple2, simple3, simple4);
    }

    public void getObjectsNonePresentInCache() {
        storeServiceWithReadCache = new StoreServiceWithReadCache(createStoreServiceWithObjects(simple1, simple2), readCache);
        Collection<Simple> objectsFromCache = storeServiceWithReadCache.getObjects(ImmutableSet.of(simple1.getId(), simple2.getId()));
        assertFoundAllObjects(objectsFromCache, simple1, simple2);
    }

    public void getNull() {
        storeServiceWithReadCache = new StoreServiceWithReadCache(createStoreServiceWithNoObjects(), readCache);
        assertThat(storeServiceWithReadCache.getObject((BubbleId<?>)null)).isNull();
    }

    public void lock() {
        readCache.put(simple1);
        SimpleId<?> simple1Id = simple1.getId();
        Simple simple1v2 = new Simple(simple1Id, "Simple 1 v2");
        storeServiceWithReadCache = new StoreServiceWithReadCache(createStoreServiceWithObjects(simple1v2), readCache);
        Simple simpleLocked = storeServiceWithReadCache.lock(simple1Id);
        assertEquals(simpleLocked, simple1v2);
        assertEquals(readCache.get(simple1Id),simple1v2 );
    }

    private void assertFoundAllObjects(Collection<? extends BubbleObject> actualObjects, BubbleObject... expectedObjects) {
        assertThat(actualObjects).hasSize(expectedObjects.length);
        HashMap<BubbleId<?>, BubbleObject> expectedMap = Maps.newHashMap();
        for (BubbleObject expectedObject : expectedObjects) {
            expectedMap.put(expectedObject.getId(), expectedObject);
        }
        for (BubbleObject actualObject : actualObjects) {
            assertEquals(actualObject, expectedMap.get(actualObject.getId()));
        }
    }
}
