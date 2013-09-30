package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Maps;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.relation.cache.annotation.Relation;
import no.statkart.skif.store.relation.cache.annotation.RelationType;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
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

    @Nullable
    private <T extends BubbleId<?>> void removeId(int level, boolean inAttachedMode, RelationName relationName, @Nullable T value, BubbleId<?> sourceId) {
        if (value != null) {
            RelationEntry inverseRelationEntry = getInverseRelation(relationName, value, !inAttachedMode);
            if (inverseRelationEntry != null) {
                inverseRelationEntry.removeId(level, sourceId);
            }
        }
    }


    @Nullable
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

    public <T extends BubbleId<?>> Set getCachedIds(int level, RelationName relationName, BubbleId<?> bubbleId) {
        RelationEntry inverseRelationEntry = getInverseRelation(relationName, bubbleId, false);
        if (inverseRelationEntry != null) {
            return inverseRelationEntry.getCachedIds(level);
        } else {
            return null;
        }
    }

    public Set setCachedIds(int level, RelationName relationName, BubbleId<?> bubbleId, Set ids) {
        RelationEntry inverseRelationEntry = getInverseRelation(relationName, bubbleId, true);
        return inverseRelationEntry.setChachedIds(level, ids);
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
        Iterator<Map.Entry<Key,RelationEntry>> iterator = inverseRelationMap.entrySet().iterator();
        while ( iterator.hasNext()) {
            Map.Entry<Key, RelationEntry> mapElement = iterator.next();
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

            if (!id.equals(key.id)) return false;
            if (!name.equals(key.name)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + id.hashCode();
            return result;
        }
    }
}
