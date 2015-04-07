package no.statkart.skif.store.relation.cache;

import no.statkart.skif.service.proxy.ProxyHandler;

import java.lang.reflect.Method;

/**
 * Baseklasse for strategien som skal anvendes ved beregning av en relasjon.
 *
 * @author Henrik Fredholm
 * @since 2.6.0
 */
public abstract class RelationStrategy {
    private enum Role implements RelationName {
        NO_CACHING
    }

    public static final RelationStrategy NO_CACHING = new RelationStrategy(Role.NO_CACHING) {
        @Override
        public Object invokeMethod(StoreRelationCache cache, ProxyHandler<?> chained, Object proxy, Method method, Object[] args) throws Throwable {
            return invokeChained(chained, proxy, method, args);
        }
    };
    protected final RelationName name;

    public RelationStrategy(RelationName relationName) {
        this.name = relationName;
    }

    protected final Object invokeChained(ProxyHandler<?> chained, Object proxy, Method method, Object[] args) throws Throwable {
        // Alle kall til underliggende metode går igjennom denne metode.
        return chained.invoke(proxy, method, args);
    }

    public abstract Object invokeMethod(StoreRelationCache cache, ProxyHandler<?> chained, Object proxy, Method method, Object[] args) throws Throwable;
}
