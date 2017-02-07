package no.statkart.skif.storetest.store;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.StoreClientReadCacheImpl;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import org.fest.assertions.api.Fail;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;

@Test
public class StoreClientReadCacheTest {

    public void getNull() {
        StoreClientReadCacheImpl readCache = new StoreClientReadCacheImpl();
        assertThat(readCache.get((BubbleId<?>)null)).isNull();
    }

    public void getWhenEmpty() {
        StoreClientReadCacheImpl readCache = new StoreClientReadCacheImpl();
        assertNull(readCache.get(new SimpleId<>(1L)));
    }

    public void getWhenPresent() {
        Cache<BubbleId<?>, BubbleObject> cache = CacheBuilder.newBuilder().build();
        Simple simple1 = new Simple(new SimpleId(1L), "Simple 1");
        cache.put(simple1.getId(), simple1);
        StoreClientReadCacheImpl readCache = new StoreClientReadCacheImpl(cache);
        Simple simpleCopy = readCache.get(simple1.getId());
        assertEqualsButNotSame(simple1, simpleCopy);
    }

    public void getAll() {
        Cache<BubbleId<?>, BubbleObject> cache = CacheBuilder.newBuilder().build();
        Simple simple1 = new Simple(new SimpleId(1L), "Simple 1");
        Simple simple2 = new Simple(new SimpleId(2L), "Simple 2");
        Simple simple3 = new Simple(new SimpleId(3L), "Simple 3");
        cache.put(simple1.getId(), simple1);
        cache.put(simple2.getId(), simple2);
        StoreClientReadCacheImpl readCache = new StoreClientReadCacheImpl(cache);
        ImmutableMap<BubbleId<?>,BubbleObject> found = readCache.getAll(ImmutableSet.of(simple1.getId(), simple2.getId(), simple3.getId()));
        assertThat(found).hasSize(2);
        assertEqualsButNotSame(simple1, (Simple) found.get(simple1.getId()));
        assertEqualsButNotSame(simple2, (Simple) found.get(simple2.getId()));
    }

    public void putWhenNotPresent() {
        SimpleId<?> simple1Id = new SimpleId(1L);
        Simple simple1 = new Simple(simple1Id, "Simple 1");
        StoreClientReadCacheImpl readCache = new StoreClientReadCacheImpl();
        Simple simpleCopy = readCache.get(simple1Id);
        assertNull(simpleCopy);
        readCache.put(simple1);
        simpleCopy = readCache.get(simple1Id);
        assertEqualsButNotSame(simple1, simpleCopy);
    }

    public void putWhenPresent() {
        SimpleId simple1Id = new SimpleId(1L);
        Simple simple1 = new Simple(simple1Id, "Simple 1");
        Simple simple1v2 = new Simple(simple1Id, "Simple 1 v. 2");
        StoreClientReadCacheImpl readCache = new StoreClientReadCacheImpl();
        readCache.put(simple1);
        assertEquals(readCache.get(simple1Id), simple1);
        readCache.put(simple1v2);
        assertEquals(readCache.get(simple1Id), simple1v2);
    }

    public void putAll() {
        Cache<BubbleId<?>, BubbleObject> cache = CacheBuilder.newBuilder().build();
        Simple simple1 = new Simple(new SimpleId(1L), "Simple 1");
        Simple simple2 = new Simple(new SimpleId(2L), "Simple 2");
        Simple simple3 = new Simple(new SimpleId(3L), "Simple 3");
        cache.put(simple1.getId(), simple1);
        StoreClientReadCacheImpl readCache = new StoreClientReadCacheImpl(cache);
        readCache.putAll(ImmutableSet.of(simple1, simple2, simple3));
        ImmutableMap<BubbleId<?>,BubbleObject> found = readCache.getAll(ImmutableSet.of(simple1.getId(), simple2.getId(), simple3.getId()));
        assertThat(found).hasSize(3);
        assertEqualsButNotSame(simple1, (Simple) found.get(simple1.getId()));
        assertEqualsButNotSame(simple2, (Simple) found.get(simple2.getId()));
    }

    public void evict() {
        Cache<BubbleId<?>, BubbleObject> cache = CacheBuilder.newBuilder().build();
        Simple simple1 = new Simple(new SimpleId(1L), "Simple 1");
        Simple simple2 = new Simple(new SimpleId(2L), "Simple 2");
        cache.put(simple1.getId(), simple1);
        cache.put(simple2.getId(), simple2);
        StoreClientReadCacheImpl readCache = new StoreClientReadCacheImpl(cache);
        ImmutableMap<BubbleId<?>, BubbleObject> foundBeforeEvict = readCache.getAll(ImmutableSet.of(simple1.getId(), simple2.getId()));
        assertThat(foundBeforeEvict).hasSize(2);
        readCache.evict(simple1.getId());
        ImmutableMap<BubbleId<?>, BubbleObject> foundAfterEvict = readCache.getAll(ImmutableSet.of(simple1.getId(), simple2.getId()));
        assertThat(foundAfterEvict).hasSize(1);
        assertEqualsButNotSame(simple2, (Simple) foundAfterEvict.get(simple2.getId()));
    }

    public void evictAll() {
        Cache<BubbleId<?>, BubbleObject> cache = CacheBuilder.newBuilder().build();
        Simple simple1 = new Simple(new SimpleId(1L), "Simple 1");
        Simple simple2 = new Simple(new SimpleId(2L), "Simple 2");
        cache.put(simple1.getId(), simple1);
        cache.put(simple2.getId(), simple2);
        StoreClientReadCacheImpl readCache = new StoreClientReadCacheImpl(cache);
        ImmutableMap<BubbleId<?>, BubbleObject> foundBeforeEvict = readCache.getAll(ImmutableSet.of(simple1.getId(), simple2.getId()));
        assertThat(foundBeforeEvict).hasSize(2);
        readCache.evictAll();
        ImmutableMap<BubbleId<?>, BubbleObject> foundAfterEvict = readCache.getAll(ImmutableSet.of(simple1.getId(), simple2.getId()));
        assertThat(foundAfterEvict).hasSize(0);
    }

    private void assertEqualsButNotSame(Simple simple1, Simple simpleCopy) {
        assertNotSame(simpleCopy, simple1);
        assertEquals(simpleCopy, simple1);
    }
}
