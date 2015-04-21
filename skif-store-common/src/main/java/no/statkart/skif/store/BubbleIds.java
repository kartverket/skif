package no.statkart.skif.store;

import com.google.common.collect.Sets;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.relation.cache.RelationName;
import no.statkart.skif.store.relation.cache.StoreRelationCache;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hjelpeklasse for generisk funksjonalitet for BubbleId som er uavhengig av BubbleId implementasjonsklasse.
 *
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BubbleIds {
    private static ConcurrentHashMap<Class, Constructor> constructorMap = new ConcurrentHashMap<Class, Constructor>();

    /**
     * Oppretter en bubbleId instans av gitt type
     */
    public static <I extends BubbleId<?>> I createInstance(Class<I> idClass, Object idValue, SnapshotVersion snapshotVersion) {
        try {
            Constructor<I> ctor = constructorMap.get(idClass);
            if (ctor == null) {
                ctor = idClass.getDeclaredConstructor(idValue.getClass(), SnapshotVersion.class);
                ctor.setAccessible(true);
                constructorMap.putIfAbsent(idClass, ctor);
            }
            I id = ctor.newInstance(idValue, snapshotVersion);
            return id;
        } catch (InstantiationException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        } catch (NoSuchMethodException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            throw new ImplementationException(e);
        }
    }

    /**
     * Returnerer hvilke klasse som BubbleId-klassen bruker som idvalue. Typisk Long eller String.
     */
    public static Class getValueType(Class<? extends BubbleId> clazz) {
        // TODO: bruke reflection på clazz istedet for å gå mot direkte AbstractBubbleId
        return AbstractBubbleId.getValueType(clazz);
    }

    //public static <T extends BubbleObject, I extends BubbleId<? extends T>> Class<? extends T> getBaseType(Class<I> clazz) {
    public static Class<? extends BubbleObject> getBaseType(Class<? extends BubbleId<?>> clazz) {
        // TODO: bruke reflection på clazz istedet for å gå mot direkte AbstractBubbleId
        return AbstractBubbleId.getTypeInfo(clazz).baseType;
    }

    public static <T extends BubbleObject> Class<? extends BubbleId<T>> getBubbleIdClass(Class<T> bubbleClass) {
        return SkifUtil.classForName(bubbleClass.getName() + "Id");
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#asIds}
     */
    @Deprecated
    public static List<BubbleId<?>> asIds(Collection<? extends BubbleObject> bubbleObjects) {
        List<BubbleId<?>> ids = new ArrayList<BubbleId<?>>(bubbleObjects.size());
        for (BubbleObject bubbleObject : bubbleObjects) {
            ids.add(bubbleObject.getId());
        }
        return ids;
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#asBaseIds}
     */
    @Deprecated
    public static List<BubbleId<?>> asBaseIds(Collection<? extends BubbleObject> bubbleObjects) {
        List<BubbleId<?>> ids = new ArrayList<BubbleId<?>>(bubbleObjects.size());
        for (BubbleObject bubbleObject : bubbleObjects) {
            BubbleId<?> id = bubbleObject.getId();
            ids.add(id.asBase());
        }
        return ids;
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#setDelegate}
     */
    @Deprecated
    static public <E extends BubbleId<?>> void setDelegate(Set<E> bubbleIds, Set<E> newElements) {
        ((AbstractBubbleIdIdSet) bubbleIds).setDelegate(newElements);
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#getDelegate}
     */
    @Deprecated
    static public <E extends BubbleId<?>> Set<E> getDelegate(Set<E> bubbleIds) {
        return ((AbstractBubbleIdIdSet) bubbleIds).delegate();
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#setFrom}
     */
    @Deprecated
    static public <E extends BubbleId<?>> void setFrom(Collection<E> collection, Set<E> newElements) {
        collection.clear();
        collection.addAll(newElements);
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#newSet}
     */
    @Deprecated
    static public <O extends BubbleObject & InverseRelationParticipation, E extends BubbleId<?>> AbstractBubbleIdIdSet<O, E> newSet(O owner, RelationName relationName) {
        return new BubbleIdSet<O, E>(owner, relationName, Sets.<E>newHashSet());
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#newSet}
     */
    @Deprecated
    static public <O extends BubbleObject & InverseRelationParticipation, E extends BubbleId<?>> AbstractBubbleIdIdSet<O, E> newSet(O owner, RelationName relationName, Set<E> set) {
        return new BubbleIdSet<O, E>(owner, relationName, set);
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#onChangeRelationImpl}
     */
    @Deprecated
    static public <O extends ComponentWithOwnerReference<?> & InverseRelationParticipation, E extends BubbleId<?>> AbstractBubbleIdIdSet<?, E> onChangeRelationImpl(O owner, RelationName relationName, Set<E> set) {
        return new ComponentBubbleIdSet<O, E>(owner, relationName, set);
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#onChangeRelationImpl}
     */
    @Deprecated
    static public <O extends ComponentWithOwnerReference<?> & InverseRelationParticipation, E extends BubbleId<?>> AbstractBubbleIdIdSet<?, E> onChangeRelationImpl(O owner, RelationName relationName) {
        return new ComponentBubbleIdSet<O, E>(owner, relationName, Sets.<E>newHashSet());
    }


    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#onChangeRelationImpl}
     */
    @Deprecated
    static final <O extends BubbleObject, T extends BubbleId<?>> void onChangeRelationImpl(O owner, RelationName relationName, T oldValue, T newValue) {
        if (owner != null) {
            if (oldValue != newValue && owner.store() != null && owner.getId().getSnapshotVersion() == SnapshotVersion.CURRENT) {
                owner.store().getRelationCache().onChangeRelation(relationName, owner.getId(), oldValue, newValue);
            }
        }
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#onChangeRelation}
     */
    @Deprecated
    public static final <O extends BubbleObject & InverseRelationParticipation, T extends BubbleId<?>> T onChangeRelation(O owner, RelationName relationName, T oldValue, T newValue) {
        onChangeRelationImpl(owner, relationName, oldValue, newValue);
        return newValue;
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#onChangeRelation}
     */
    @Deprecated
    public static <T extends BubbleId<?>> T onChangeRelation(ComponentWithOwnerReference<?> component, RelationName relationName, T oldValue, T newValue) {
        BubbleObject owningBubble = Components.getOwningBubble(component);
        Bubbles.onChangeRelationImpl(owningBubble,relationName, oldValue, newValue);
        return newValue;
    }

    /**
     * @deprecated Metoden er flyttet til {@link Bubbles#getRelationCacheIfBubbleAttachedToStoreAndCacheEnabledOtherwiseNull}
     */
    @Deprecated
    public static StoreRelationCache getRelationCacheIfBubbleAttachedToStoreAndCacheEnabledOtherwiseNull(BubbleObject owningBubble) {
        if (owningBubble != null && owningBubble.store() != null) {
            StoreRelationCache relationCache = owningBubble.store().getRelationCache();
            return relationCache.isEnabled() ? relationCache : null;
        } else {
            return null;
        }
    }
}
