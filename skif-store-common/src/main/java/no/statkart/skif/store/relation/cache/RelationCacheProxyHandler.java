package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.store.BubbleId;

import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * ProxyHandler for caching av relasjoner. Denne proxyhandler legges i {@code CallServiceChain} på klient og server
 * for de services som implementerer invers domene finders.
 *
 * <P>Proxy-en har til oppgave å bruke cachet relasjoner der hvor de allerede finnes og hente opp og cache
 * etterspurte relasjoner som ikke finnes. Relasjonscaching  styres via {@link StoreRelationCache}
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class RelationCacheProxyHandler<S> extends ChainedProxyHandler<S> {
    final Provider<StoreRelationCache> cacheProvider;


    @Inject
    public RelationCacheProxyHandler(Provider<StoreRelationCache> cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        RelationName name = cacheProvider.get().getRelationNameReturnNullIfDisabled(method);
        if (name != null) {
            checkArgument(args.length==1, "Unexpected argument length: %d", args.length);
            checkArgument(args[0] instanceof Collection, "Expected collection of bubble ids as argument");
            result = useCaching(name, proxy, method, ((Collection<BubbleId<?>>) args[0]));
        } else {
            result = chained.invoke(proxy,method, args);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected Map<BubbleId<?>, Object> noCaching(Object proxy, Method method, Object[] args) throws Throwable {
        return (Map<BubbleId<?>, Object>) chained.invoke(proxy,method, args);
    }

    private Map<BubbleId<?>, Object> useCaching(RelationName name, Object proxy, Method method, Collection<BubbleId<?>> ids) throws Throwable {
        Map<BubbleId<?>, Object> mapOfResults = Maps.newHashMapWithExpectedSize(ids.size());
        StoreRelationCache cache = cacheProvider.get();

        Collection<BubbleId<?>> missingIds = cache.findNonMaterialized(name, ids);
        if (!missingIds.isEmpty()) {
            Object[] args = {missingIds};
            Map<BubbleId<?>, Object> uncachedMap = noCaching(proxy, method, args);
            for (Map.Entry<BubbleId<?>, Object> entry : uncachedMap.entrySet()) {
                Object updatedCachedRelationValue = cache.setRelationValue(name, entry.getKey(), entry.getValue());
                mapOfResults.put(entry.getKey(), updatedCachedRelationValue);
            }
        }

        Set<BubbleId<?>> restIds = Sets.newHashSet(ids);
        restIds.removeAll(mapOfResults.keySet());

        for (BubbleId<?> id : ids) {
            RelationValueHolder cachedRelationValueHolder = cache.getRelationValue(name, id);
            if (cachedRelationValueHolder != null) {
                mapOfResults.put(id, cachedRelationValueHolder.getValue());
            } else {
                throw new ImplementationException("There were still unmaterialized relations");
            }
        }

        return mapOfResults;
    }

}
