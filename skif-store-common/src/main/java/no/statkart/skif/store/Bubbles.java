package no.statkart.skif.store;

import com.google.common.collect.Sets;
import no.statkart.skif.store.relation.cache.RelationName;
import no.statkart.skif.store.relation.cache.StoreRelationCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author Henrik Fredholm
 * @since 2.6.0
 */
public class Bubbles {

    public static List<BubbleId<?>> asIds(Collection<? extends BubbleObject> bubbleObjects) {
        List<BubbleId<?>> ids = new ArrayList<BubbleId<?>>(bubbleObjects.size());
        for (BubbleObject bubbleObject : bubbleObjects) {
            ids.add(bubbleObject.getId());
        }
        return ids;
    }

    public static List<BubbleId<?>> asBaseIds(Collection<? extends BubbleObject> bubbleObjects) {
        List<BubbleId<?>> ids = new ArrayList<BubbleId<?>>(bubbleObjects.size());
        for (BubbleObject bubbleObject : bubbleObjects) {
            BubbleId<?> id = bubbleObject.getId();
            ids.add(id.asBase());
        }
        return ids;
    }

    @SuppressWarnings("unchecked")
    static public <E> void setDelegate(Set<E> inverseValues, Set<E> newElements) {
        ((AbstractInverseValueSet) inverseValues).setDelegate(newElements);
    }

    @SuppressWarnings("unchecked")
    static public <E> Set<E> getDelegate(Set<E> inverseValues) {
        return ((AbstractInverseValueSet) inverseValues).delegate();
    }

    static public <E> void setFrom(Collection<E> collection, Set<E> newElements) {
        collection.clear();
        collection.addAll(newElements);
    }

    static public <O extends BubbleObject & InverseRelationParticipation, E> AbstractInverseValueSet<O, E> newSet(O owner, RelationName relationName) {
        return new InverseValueSet<O, E>(owner, relationName, Sets.<E>newHashSet());
    }

    static public <O extends BubbleObject & InverseRelationParticipation, E> AbstractInverseValueSet<O, E> newSet(O owner, RelationName relationName, Set<E> set) {
        return new InverseValueSet<O, E>(owner, relationName, set);
    }

    static public <O extends ComponentWithOwnerReference<?> & InverseRelationParticipation, E> AbstractInverseValueSet<?, E> newSet(O owner, RelationName relationName, Set<E> set) {
        return new ComponentInverseValueSet<O, E>(owner, relationName, set);
    }

    static public <O extends ComponentWithOwnerReference<?> & InverseRelationParticipation, E> AbstractInverseValueSet<?, E> newSet(O owner, RelationName relationName) {
        return new ComponentInverseValueSet<O, E>(owner, relationName, Sets.<E>newHashSet());
    }


    static final <O extends BubbleObject, E> void onChangeRelationImpl(O owner, RelationName relationName, E oldValue, E newValue) {
        if (owner != null) {
            if (oldValue != newValue && owner.store() != null && owner.getId().getSnapshotVersion() == SnapshotVersion.CURRENT) {
                owner.store().getRelationCache().onChangeRelation(relationName, owner.getId(), oldValue, newValue);
            }
        }
    }

    public static final <O extends BubbleObject & InverseRelationParticipation, E> E onChangeRelation(O owner, RelationName relationName, E oldValue, E newValue) {
        onChangeRelationImpl(owner, relationName, oldValue, newValue);
        return newValue;
    }

    /**
     *  @deprecated bruk identisk metode  {@link Components#onChangeRelation}
     */
    public static <E> E onChangeRelation(ComponentWithOwnerReference<?> component, RelationName relationName, E oldValue, E newValue) {
        BubbleObject owningBubble = Components.getOwningBubble(component);
        Bubbles.onChangeRelationImpl(owningBubble, relationName, oldValue, newValue);
        return newValue;
    }

    public static StoreRelationCache getRelationCacheIfBubbleAttachedToStoreAndCacheEnabledOtherwiseNull(BubbleObject owningBubble) {
        if (owningBubble != null && owningBubble.store() != null) {
            StoreRelationCache relationCache = owningBubble.store().getRelationCache();
            return relationCache.isEnabled() ? relationCache : null;
        } else {
            return null;
        }
    }
}
