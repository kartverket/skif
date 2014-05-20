package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.relation.cache.annotation.Relation;
import no.statkart.skif.store.relation.cache.annotation.RelationType;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Klasse for caching av invers relasjoner i Store.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class RelationCache {
    Map<Key, RelationEntry> inverseRelationMap = Maps.newHashMap();

    public <T extends BubbleId<?>> void onChangeRelation(int level, boolean inAttachedMode, RelationName relationName, BubbleId<?> sourceId, @Nullable T oldValue, @Nullable T newValue) {
        removeId(level, inAttachedMode, relationName, oldValue, sourceId);
        addId(level, inAttachedMode, relationName, newValue, sourceId);
    }

    private <T extends BubbleId<?>> void removeId(int level, boolean inAttachedMode, RelationName relationName, @Nullable T value, BubbleId<?> sourceId) {
        if (value != null) {
            RelationEntry inverseRelationEntry = getInverseRelation(relationName, value, !inAttachedMode);
            if (inverseRelationEntry != null) {
                inverseRelationEntry.removeId(level, sourceId);
            }
        }
    }


    private <T extends BubbleId<?>> void addId(int level, boolean inAttachedMode, RelationName relationName, @Nullable T value, BubbleId<?> sourceId) {
        if (value != null) {
            RelationEntry inverseRelationEntry = getInverseRelation(relationName, value, !inAttachedMode);
            if (inverseRelationEntry != null) {
                inverseRelationEntry.addId(level, sourceId);
            }
        }
    }

    private <T extends BubbleId<?>> RelationEntry getInverseRelation(RelationName relationName, T value, boolean create) {
        Key key = new Key(relationName, value);
        RelationEntry inverseRelationEntry = inverseRelationMap.get(key);
        if (inverseRelationEntry == null && create) {
            inverseRelationEntry = new RelationEntry();
            inverseRelationMap.put(key, inverseRelationEntry);
        }
        return inverseRelationEntry;
    }

    public RelationValueHolder getRelationValue(int level, RelationName relationName, BubbleId<?> bubbleId) {
        RelationEntry inverseRelationEntry = getInverseRelation(relationName, bubbleId, false);
        if (inverseRelationEntry != null) {
            return new RelationValueHolder(inverseRelationEntry.getRelationValue(level));
        } else {
            return null;
        }
    }

    public Object setRelationValue(int level, RelationName relationName, BubbleId<?> bubbleId, Object relationValue) {
        RelationEntry inverseRelationEntry = getInverseRelation(relationName, bubbleId, true);
        return inverseRelationEntry.setRelationValue(level, relationValue);
    }

    public RelationName getRelationName(Method method) {
        //TODO optimaliser via singleton lookup for method. Dette endre seg ikke og kan derfor caches på tvers av alle sessions
        RelationName name=null;
        Relation annotation = method.getAnnotation(Relation.class);
        if (annotation!=null) {
            checkState(annotation.type()== RelationType.INVERSE);
            Class<? extends Enum> enumClass = SkifUtil.classForName(method.getDeclaringClass().getCanonicalName()+"$Role");
            name = (RelationName) Enum.valueOf(enumClass, annotation.name());
        }
        return name;
    }

    public void onCommitUnitOfWork(int level) {
        for (Map.Entry<Key, RelationEntry> mapElement : inverseRelationMap.entrySet()) {
            RelationEntry entry = mapElement.getValue();
            entry.commitEntry(level);
        }
    }

    public void onAbortUnitOfWork(int level) {
        Iterator<Map.Entry<Key,RelationEntry>> iterator = inverseRelationMap.entrySet().iterator();
        while ( iterator.hasNext()) {
            Map.Entry<Key, RelationEntry> mapElement = iterator.next();
            RelationEntry entry = mapElement.getValue();
            entry.abortEntry(level);
            if (entry.hasNoRelationsInRemainigLevels(level-1)) {
                iterator.remove();
            }
        }
    }

    public Collection<BubbleId<?>> findNonMaterialized(int level, RelationName relationName, Collection<BubbleId<?>> ids) {
        Set<BubbleId<?>> missingIds = Sets.newHashSet();
        for (BubbleId<?> bubbleId : ids) {
            RelationEntry inverseRelationEntry = getInverseRelation(relationName, bubbleId, false);
            if (inverseRelationEntry == null) {
                missingIds.add(bubbleId);
            } else {
                boolean materialized = false;
                for (int i = level; i >= 0; --i) {
                    if (inverseRelationEntry.isMaterialized(i)) {
                        materialized = true;
                    }
                }
                if (!materialized) {
                    missingIds.add(bubbleId);
                }
            }
        }
        return missingIds;
    }

    static class Key {
        private final RelationName name;
        private final Object id;

        Key(RelationName name, Object id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            return id.equals(key.id) && name.equals(key.name);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + id.hashCode();
            return result;
        }
    }
}
