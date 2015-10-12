package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Lists;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.*;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * En facade for håndtering av relation caching i Store. Klassen henter ut aktivt UnitOfWork level fra Store og
 * bruker dette level i kall videre til {@link RelationCache}.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public abstract class StoreRelationCache {
    protected RelationCache relationCache = new RelationCache();
    protected final Store store;
    protected final Provider<RelationCacheRegistry> cacheRegistryProvider = new Provider<RelationCacheRegistry>() {
        @Override
        public RelationCacheRegistry get() {
            return store.getInstance(RelationCacheRegistry.class);
        }
    };

    protected StoreRelationCache(Store store) {
        this.store = store;
    }

    protected abstract WrappableStoreSession getStoreSession();

    protected final int getLevel() {
        return getStoreSession().getLevel();
    }

    protected final boolean inAttachedMode() {
        return getStoreSession().inAttachedMode();
    }

    public boolean isEnabled() {
        return relationCache.isEnabled(getLevel());
    }

    /**
     * Enabler og disabler relation caching. Ved disabling evictes alle cachet relasjoner.
     */
    public void setEnabled(boolean enabled) {
        relationCache.setEnabled(getLevel(), enabled);
    }

    public RelationStrategy getStrategy(Method method) {
        if (isEnabled()) {
            return cacheRegistryProvider.get().getStrategy(method);
        } else {
            return null;
        }
    }

    public <E> void onChangeRelation(RelationName relationName, BubbleId<?> sourceId, @Nullable E oldValue, @Nullable E newValue) {
        if (isEnabled()) {
            relationCache.onChangeRelation(getLevel(), inAttachedMode(), relationName, sourceId, oldValue, newValue);
        }
    }

    public <E> RelationValueHolder getRelationValue(RelationName relationName, E value) {
        checkState(isEnabled());
        return relationCache.getRelationValue(getLevel(), relationName, value);
    }

    public <E> Object setRelationValue(RelationName relationName, E value, Object relationValue) {
        checkState(isEnabled());
        return relationCache.setRelationValue(getLevel(), relationName, value, relationValue);
    }

    public RelationName getRelationNameReturnNullIfDisabled(Method method) {
        if (isEnabled()) {
            return relationCache.getRelationName(method);
        } else {
            return null;
        }
    }

    public boolean transferToCache(RelationName relationName, BubbleId<?> id, Object cachedRelationValue) {
        if (isEnabled()) {
            relationCache.setRelationValue(getLevel(), relationName, id, cachedRelationValue);
        }
        return isEnabled();
    }


    public RelationFinder getRelationFinder(RelationName relationName, Store store) {
        return new RelationFinder(relationName, store);
    }

    public void materialiseRequestedRelations(BubbleObject bubbleObject) {
        if (bubbleObject != null) {
            for (InverseRelation<?> inverseRelation : getInverseRelations(bubbleObject)) {
                inverseRelation.setCachedIfRequested();
            }
        }
    }

    public void materialiseRequestedRelations(Collection<? extends BubbleObject> bubbleObjects) {
        // TODO: Optimaliser. Sorter på type og umaterialisert. Bruk finder til å hente verdier og sette cachet verdi direkte.
        for (BubbleObject bubbleObject : bubbleObjects) {
            materialiseRequestedRelations(bubbleObject);
        }
    }

    private List<InverseRelation<?>> getInverseRelations(BubbleObject bubbleObject) {
        // TODO: opptimaliser. Cache relasjons-metoder per klasse i trådsikker singleton
        List<InverseRelation<?>> result = Lists.newArrayList();
        for (Method method : bubbleObject.getClass().getMethods()) {
            if (InverseRelation.class.isAssignableFrom(method.getReturnType())) {
                try {
                    result.add((InverseRelation<?>) method.invoke(bubbleObject));
                } catch (IllegalAccessException e) {
                    throw new ImplementationException(e);
                } catch (InvocationTargetException e) {
                    throw new ImplementationException(e);
                }
            }
        }
        return result;
    }


    public void cacheMaterialisedRelations(@Nullable BubbleObject bubbleObject) {
        if (bubbleObject != null) {
            checkState(bubbleObject.store() == store, "BubbleObject er ikke registrert i inneværende Store");
            for (InverseRelation<?> inverseRelation : getInverseRelations(bubbleObject)) {
                if (inverseRelation.isMaterialised()) {
                    if (isEnabled()) {
                        relationCache.setRelationValue(getLevel(), inverseRelation.getName(), bubbleObject.getId(), inverseRelation.getCached());
                    }
                    inverseRelation.setCached(null);
                    inverseRelation.setMaterialised(false);
                }
            }
        }
    }

    public void cacheMaterialisedRelations(Collection<? extends BubbleObject> bubbleObjects) {
        for (BubbleObject bubbleObject : bubbleObjects) {
            cacheMaterialisedRelations(bubbleObject);
        }
    }

    public <T> Collection<T> findNonMaterialized(RelationName relationName, Collection<T> ids) {
        checkState(isEnabled());
        return relationCache.findNonMaterialized(getLevel(), relationName, ids);
    }

    public void updateRemoved(BubbleId<?> owningBubbleId, InverseRelationParticipation oldInstance) {
        InverseRelationCollector collector = new InverseRelationCollector();
        oldInstance.collectInverseRelationValues(collector);
        for (Map.Entry<RelationName, Object> entry : collector.entrySet()) {
            Object inverseValue = entry.getValue();
            if (inverseValue instanceof InverseRelationCollector.Values) {
                for (Object v : ((InverseRelationCollector.Values) inverseValue)) {
                    onChangeRelation(entry.getKey(), owningBubbleId, v, null);
                }
            } else if (inverseValue instanceof Collection) {
                for (Object v : (Collection) inverseValue) {
                    onChangeRelation(entry.getKey(), owningBubbleId, v, null);
                }
            } else {
                onChangeRelation(entry.getKey(), owningBubbleId, inverseValue, null);
            }
        }
    }

    public void updateAdded(BubbleId<?> owningBubbleId, InverseRelationParticipation newInstance) {
        InverseRelationCollector collector = new InverseRelationCollector();
        newInstance.collectInverseRelationValues(collector);
        for (Map.Entry<RelationName, Object> entry : collector.entrySet()) {
            Object inverseValue = entry.getValue();
            if (inverseValue instanceof InverseRelationCollector.Values) {
                for (Object v : ((InverseRelationCollector.Values) inverseValue)) {
                    onChangeRelation(entry.getKey(), owningBubbleId, null, v);
                }
            } else if (inverseValue instanceof Collection) {
                for (Object v : (Collection) inverseValue) {
                    onChangeRelation(entry.getKey(), owningBubbleId, null, v);
                }
            } else {
                onChangeRelation(entry.getKey(), owningBubbleId, null, inverseValue);
            }
        }
    }

    public void evictAll() {
        relationCache.evictAll();
    }
}
