package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Lists;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.*;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

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
    protected boolean enabled;
    protected final Store store;

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
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public <T extends BubbleId<?>> void onChangeRelation(RelationName relationName, BubbleId<?> sourceId, @Nullable T oldValue, @Nullable T newValue) {
        if (enabled) {
            relationCache.onChangeRelation(getLevel(), inAttachedMode(), relationName, sourceId, oldValue, newValue);
        }
    }

    public RelationValueHolder getRelationValue(RelationName relationName, BubbleId<?> id) {
        checkState(enabled);
        return relationCache.getRelationValue(getLevel(), relationName, id);
    }

    public Object setRelationValue(RelationName relationName, BubbleId<?> id, Object relationValue) {
        checkState(enabled);
        return relationCache.setRelationValue(getLevel(), relationName, id, relationValue);
    }

    public RelationName getRelationNameReturnNullIfDisabled(Method method) {
        if (enabled) {
            return relationCache.getRelationName(method);
        } else {
            return null;
        }
    }

    public boolean transferToCache(RelationName relationName, BubbleId<?> id, Object cachedRelationValue) {
        if (enabled) {
            relationCache.setRelationValue(getLevel(), relationName, id, cachedRelationValue);
        }
        return enabled;
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
                    if (enabled) {
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

    public Collection<BubbleId<?>> findNonMaterialized(RelationName relationName, Collection<BubbleId<?>> ids) {
        checkState(enabled);
        return relationCache.findNonMaterialized(getLevel(), relationName, ids);
    }
}
