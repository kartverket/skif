package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.store.BubbleId;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Strategi for håndtering av inverse relasjoner. Denne strategi er basert på at endringer i releasjoner registreres
 * kontinuerlig i cachen. For å få dette til må objektet som eier relasjonen implementere setter-metoden slik at den
 * gjør kall til cachen med gammel og ny verdi. For collection-releasjoner må disse wrappes slik at cachen oppdateres
 * automatisk når det utføres endringer direkte på collectionene utenom objektets setter-metode. I tillegg må objektet
 * som eier relasjonen implementere en metode som gjør det mulig for Store automatisk å hente ut alle releasjonen ifm
 * registrering av endrede objekter i Store.
 * <p/>
 * Denne strategi gjør at releasjonscachen alltid er up-to-date så lenge objektet er knyttet til Store. Cachen er ikke
 * avhengig av at det gjøres kall til store.update() først.
 *
 * @author Henrik Fredholm
 * @since 2.6.0
 */
public class InverseRelationStrategy extends RelationStrategy {
    @SuppressWarnings("WeakerAccess")
    public InverseRelationStrategy(RelationName relationName) {
        super(relationName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invokeMethod(StoreRelationCache cache, ProxyHandler<?> chained, Object proxy, Method method, Object[] args) throws Throwable {
        checkArgument(args.length == 1, "Unexpected argument length: %d", args.length);
        checkArgument(args[0] instanceof Collection, "Expected collection of bubble ids as argument");
        return useCaching(cache, chained, proxy, method, (Collection<BubbleId<?>>) args[0]);
    }

    private <T> Map<T, Object> useCaching(StoreRelationCache cache, ProxyHandler<?> chained, Object proxy, Method method, Collection<T> inverseValues) throws Throwable {
        Map<T, Object> mapOfResults = Maps.newHashMapWithExpectedSize(inverseValues.size());

        Collection<T> missingInverseValues = cache.findNonMaterialised(name, inverseValues);
        if (!missingInverseValues.isEmpty()) {
            Object[] args = {missingInverseValues};
            Map<T, Object> uncachedMap = noCaching(chained, proxy, method, args);
            // Støtter egentlig ikke multithreaded adgang, men gjør en ekstra sjekk her i tilfellet en annen tråd
            // har lastet relasjonene i mellomtiden. Det vil fange de fleste tilfeller siden det er lastingen som tar tid.
            Collection<T> missingInverseValuesAfterLoading = cache.findNonMaterialised(name, inverseValues);
            for (Map.Entry<T, Object> entry : uncachedMap.entrySet()) {
                if (missingInverseValuesAfterLoading.contains(entry.getKey())) {
                    cache.materialiseRelation(name, entry.getKey(), entry.getValue());
                    Object updatedCachedRelationValue = cache.getRelationValueHolder(name, entry.getKey()).getValue();
                    mapOfResults.put(entry.getKey(), updatedCachedRelationValue);
                }
            }
        }

        Set<T> restOfInverseValues = Sets.newHashSet(inverseValues);
        restOfInverseValues.removeAll(mapOfResults.keySet());

        for (T inverseValue : inverseValues) {
            RelationValueHolder cachedRelationValueHolder = cache.getRelationValueHolder(name, inverseValue);
            if (cachedRelationValueHolder != null) {
                mapOfResults.put(inverseValue, cachedRelationValueHolder.getValue());
            } else {
                throw new ImplementationException("There were still unmaterialized relations");
            }
        }

        return mapOfResults;
    }

    @SuppressWarnings({"unchecked", "WeakerAccess"})
    protected <T> Map<T, Object> noCaching(ProxyHandler<?> chained, Object proxy, Method method, Object[] args) throws Throwable {
        return (Map<T, Object>) invokeChained(chained, proxy, method, args);
    }
}
