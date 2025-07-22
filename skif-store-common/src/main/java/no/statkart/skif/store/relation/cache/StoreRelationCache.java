package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Lists;
import jakarta.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractStoreSession;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.BubbleObjectWithIdent;
import no.statkart.skif.store.InverseRelation;
import no.statkart.skif.store.InverseRelationCollector;
import no.statkart.skif.store.InverseRelationParticipation;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.WrappableStoreSession;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.google.common.base.Preconditions.checkState;

/**
 * En facade for håndtering av relation caching i Store. Klassen henter ut aktivt UnitOfWork level fra Store og
 * bruker dette level i kall videre til {@link RelationCache}.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@SuppressWarnings("WeakerAccess")
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

    public boolean isEnabled(int level) {
        return relationCache.isEnabled(level);
    }
    /**
     * Enabler og disabler relation caching. Ved disabling evictes alle cachet relasjoner.
     */
    public void setEnabled(boolean enabled) {
        int level = getLevel();
        if (enabled != relationCache.isEnabled(level)) {
            if (enabled) {
                // Store enables her og alle underliggende UnitOfWork er disabled (fordi cachinging auto enables når
                // underliggende UnitOfWork har caching enabled og i slike tilfeller kan caching ikke disables).
                relationCache.setEnabled(level, true);
                AbstractStoreSession storeSession = (AbstractStoreSession) getStoreSession();
                storeSession.onEnableRelationCache(level);
            } else {
                relationCache.setEnabled(level, false);
            }
        }
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


    /**
     * Hjelpemetode for testing som ikke ellers bør brukes
     */
    public <E> RelationTracker peekRelationTracker(RelationName relationName, E value) {
        checkState(isEnabled());
        return relationCache.peekRelationTracker(getLevel(), relationName, value);
    }


    public <E> RelationValueHolder getRelationValueHolder(RelationName relationName, E value) {
        checkState(isEnabled());
        return relationCache.getRelationValueHolder(getLevel(), relationName, value);
    }

    public <E> void materialiseRelation(RelationName relationName, E value, Object relationValue) {
        checkState(isEnabled());
        relationCache.materialiseRelation(getLevel(), relationName, value, relationValue);
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
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ImplementationException(e);
                }
            }
        }
        return result;
    }

    public <E> boolean isMaterialised(RelationName relationName, E value) {
        return isEnabled() && relationCache.isMaterialised(getLevel(), relationName, value);
    }

    public void cacheMaterialisedRelationsAndClearLocallyCachedValues(@Nullable BubbleObject bubbleObject, int level) {
        if (bubbleObject != null) {
            for (InverseRelation<?> inverseRelation : getInverseRelations(bubbleObject)) {
                if (inverseRelation.isMaterialised()) {
                    if (isEnabled()) {
                        // TODO: Her må vi egentlig vite om oppdateringen kommer fra en UnitOfWorkTransfer (som kan inneholde endret state) eller en Transfer (som ikke kan inneholde endret state).
                        // TODO: Hvis vi har endret state skal verdienen ikke settes på underliggende levels. Det blir feil ved abort.
                        relationCache.materialiseRelation(level, inverseRelation.getName(), bubbleObject.getId(), inverseRelation.getCached());
                    }
                    inverseRelation.setCached(null);
                    inverseRelation.setMaterialised(false);
                }
            }
        }
    }

    public <T> Collection<T> findNonMaterialised(RelationName relationName, Collection<T> ids) {
        checkState(isEnabled());
        return relationCache.findNonMaterialised(getLevel(), relationName, ids);
    }

    public void updateRemoved(BubbleId<?> owningBubbleId, InverseRelationParticipation oldInstance) {
        updateRemoved(owningBubbleId, oldInstance, new Executor() {
            @Override
            public void execute(@Nonnull Runnable command) {
                command.run();
            }
        });
    }

    @SuppressWarnings("Duplicates")
    public void updateRemoved(BubbleId<?> owningBubbleId, final InverseRelationParticipation oldInstance, Executor collectionExecutor) {
        final InverseRelationCollector collector = new InverseRelationCollector();
        collectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                oldInstance.collectInverseRelationValues(collector);
            }
        });
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

    public void onIdentRemoved(BubbleObjectWithIdent<?> bubbleObjectWithIdent) {
        // SKIF-599: Nødvendig med kall til 'onIdentChanged' her for å fremtvinge at det opprettes en entry
        // i relationCache hvis det ikke finnes en fra før. Derved kan 'onSourceIdRemoved' kan plukke opp
        // relationname og ident for boblen og angi at identen ikke lengre er koplet til boblen.
        bubbleObjectWithIdent.onIdentChanged();
        relationCache.onSourceIdRemoved(getLevel(), bubbleObjectWithIdent.getId());
    }

    @SuppressWarnings("Duplicates")
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
        if (isEnabled()) {
            setEnabled(false);  // Ved disabling evictes cachen automatisk
            setEnabled(true);   // Ved enabling så gjenberegnes caching for låste objekter og har hensyn til endret relasjoner i disse
        }
    }
}
