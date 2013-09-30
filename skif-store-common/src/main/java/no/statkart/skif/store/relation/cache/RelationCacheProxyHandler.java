package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;

import java.lang.reflect.Method;
import java.util.*;

/**
 * ProxyHandler for caching av relasjoner. Denne proxyhandler legges i {@code CallServiceChain} på klient og server
 * for de services som implementerer invers domene finders.
 *
 * <P>Proxy-en har til oppgave å bruke cachet relasjoner der hvor de allerede finnes og hente opp og cache
 * etterspurte relasjoner som ikke finnes. Hvilke relasjoner som caches styres via {@link StoreRelationCache}
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class RelationCacheProxyHandler<S> extends ChainedProxyHandler<S> {
    final StoreRelationCache cache;


    @Inject
    public RelationCacheProxyHandler(StoreRelationCache cache) {
        this.cache = cache;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Object mapOfResults;
        RelationName name = cache.getRelationNameReturnNullIfDisabled(method);
        if (name != null) {
            mapOfResults = useCaching(name, proxy, method, ((Collection<BubbleId<?>>) args[0]));
        } else {
            mapOfResults = noCaching(proxy, method, args);
        }
        return mapOfResults;
    }

    protected Map<BubbleId<?>, Set<BubbleId<?>>> noCaching(Object proxy, Method method, Object[] args) throws Throwable {
        return (Map<BubbleId<?>, Set<BubbleId<?>>>) chained.invoke(proxy,method, args);
    }

    private Map<BubbleId<?>, Set<BubbleId<?>>> useCaching(RelationName name, Object proxy, Method method, Collection<BubbleId<?>> ids) throws Throwable {
        Map<BubbleId<?>, Set<BubbleId<?>>> mapOfResults = Maps.newHashMapWithExpectedSize(ids.size());
        List<BubbleId<?>> missingIds = null;
        for (BubbleId<?> id : ids) {
            Set cachedIds = cache.getCachedIds(name, id);
            if (cachedIds != null) {
                mapOfResults.put(id, cachedIds);
            } else {
                if (missingIds == null) {
                    missingIds = new ArrayList<BubbleId<?>>();
                }
                missingIds.add(id);
            }
        }
        if (missingIds != null) {
            Object[] args = {missingIds};
            Map<BubbleId<?>, Set<BubbleId<?>>> uncachedMap = noCaching(proxy, method, args);
            for (Map.Entry<BubbleId<?>, Set<BubbleId<?>>> entry : uncachedMap.entrySet()) {
                Set<BubbleId<?>> updatedCachedSet = cache.setCachedIds(name, entry.getKey(), entry.getValue());
                mapOfResults.put(entry.getKey(), updatedCachedSet);
            }
        }
        return mapOfResults;
    }


}
