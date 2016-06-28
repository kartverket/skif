package no.statkart.skif.store;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import no.statkart.skif.util.CopyHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public class StoreClientReadCacheImpl implements  StoreClientReadCache {
    private final Cache<BubbleId<?>, BubbleObject> cache;

    public StoreClientReadCacheImpl() {
        this(CacheBuilder.newBuilder()
                .maximumSize(10000)
                .<BubbleId<?>, BubbleObject>build());
    }

    public StoreClientReadCacheImpl(Cache<BubbleId<?>, BubbleObject> cache) {
        this.cache = cache;
    }

    @Override
    public void put(BubbleObject bubbleObject) {
        cache.put(bubbleObject.getId(), bubbleObject);
    }

    @Override
    public void putAll(Collection<? extends BubbleObject> objects) {
        for (BubbleObject object : objects) {
            cache.put(object.getId(), object);
        }
    }

    @Override
    @Nullable
    public <T extends BubbleObject> T get(@Nullable BubbleId<? extends T> id) {
        return id == null ? null : (T) CopyHelper.copy(cache.getIfPresent(id));
    }

    @Override
    public ImmutableMap<BubbleId<?>, BubbleObject> getAll(Iterable<?> ids) {
        ImmutableMap<BubbleId<?>, BubbleObject> allPresent = cache.getAllPresent(ids);
        return createCopy(allPresent);
    }

    private ImmutableMap<BubbleId<?>, BubbleObject> createCopy(ImmutableMap<BubbleId<?>, BubbleObject> allPresent) {
        ImmutableMap.Builder<BubbleId<?>, BubbleObject> builder = ImmutableMap.builder();
        for (Map.Entry<BubbleId<?>, BubbleObject> entry : allPresent.entrySet()) {
            builder.put(entry.getKey(), CopyHelper.copy(entry.getValue()));
        }
        return builder.build();
    }

    @Override
    public void evict(@Nullable BubbleId<?> id) {
        if (id != null) {
            cache.invalidate(id);
        }
    }

    @Override
    public void evictAll() {
        cache.invalidateAll();
    }
}
