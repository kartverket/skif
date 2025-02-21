package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.store.BubbleId;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * ProxyHandler for caching av relasjoner. Denne proxyhandler legges i {@code CallServiceChain} på klient og server
 * for de services som implementerer invers domene finders.
 * <p/>
 * Proxy-en har til oppgave å bruke cachet relasjoner der hvor de allerede finnes og hente opp og cache
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
        final StoreRelationCache relationCache = cacheProvider.get();
        RelationStrategy strategy = relationCache.getStrategy(method);
        if (strategy != null) {
            result = strategy.invokeMethod(relationCache, chained, proxy, method, args);
        } else {
            result = chained.invoke(proxy, method, args);
        }
        return result;
    }
}
