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
 * Klasse for caching av invers relasjoner i Store. Invers relasjonen kan enten være fra en bobleId eller et
 * value objekt som brukes index key (f.eks en streng).
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class RelationCache {
    Map<Key, RelationEntry> inverseRelationMap = Maps.newHashMap();

    public <E> void onChangeRelation(int level, boolean inAttachedMode, RelationName relationName, BubbleId<?> sourceId, @Nullable E oldInverseValue, @Nullable E newInverseValue) {
        removeId(level, inAttachedMode, relationName, oldInverseValue, sourceId);
        addId(level, inAttachedMode, relationName, newInverseValue, sourceId);
    }

    private <E> void removeId(int level, boolean inAttachedMode, RelationName relationName, @Nullable E inverseValue, BubbleId<?> sourceId) {
        if (inverseValue != null) {
            RelationEntry inverseRelationEntry = getInverseRelation(relationName, inverseValue, !inAttachedMode);
            if (inverseRelationEntry != null) {
                inverseRelationEntry.removeId(level, sourceId);
            }
        }
    }


    private <E> void addId(int level, boolean inAttachedMode, RelationName relationName, @Nullable E inverseValue, BubbleId<?> sourceId) {
        if (inverseValue != null) {
            RelationEntry inverseRelationEntry = getInverseRelation(relationName, inverseValue, !inAttachedMode);
            if (inverseRelationEntry != null) {
                inverseRelationEntry.addId(level, sourceId);
            }
        }
    }

    private <E> RelationEntry getInverseRelation(RelationName relationName, E inverseValue, boolean create) {
        Key key = new Key(relationName, inverseValue);
        RelationEntry inverseRelationEntry = inverseRelationMap.get(key);
        if (inverseRelationEntry == null && create) {
            inverseRelationEntry = new RelationEntry();
            inverseRelationMap.put(key, inverseRelationEntry);
        }
        return inverseRelationEntry;
    }

    public <E> RelationValueHolder getRelationValue(int level, RelationName relationName, E inverseValue) {
        RelationEntry inverseRelationEntry = getInverseRelation(relationName, inverseValue, false);
        if (inverseRelationEntry != null) {
            return new RelationValueHolder(inverseRelationEntry.getRelationValue(level));
        } else {
            return null;
        }
    }

    public <E> Object setRelationValue(int level, RelationName relationName, E inverseValue, Object relationValue) {
        RelationEntry inverseRelationEntry = getInverseRelation(relationName, inverseValue, true);
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

    public <E> Collection<E> findNonMaterialized(int level, RelationName relationName, Collection<E> inverseValues) {
        Set<E> missingInverseValues = Sets.newHashSet();
        for (E inverseValue : inverseValues) {
            RelationEntry inverseRelationEntry = getInverseRelation(relationName, inverseValue, false);
            if (inverseRelationEntry == null) {
                missingInverseValues.add(inverseValue);
            } else {
                boolean materialized = false;
                for (int i = level; i >= 0; --i) {
                    if (inverseRelationEntry.isMaterialized(i)) {
                        materialized = true;
                    }
                }
                if (!materialized) {
                    missingInverseValues.add(inverseValue);
                }
            }
        }
        return missingInverseValues;
    }

    static class Key {
        private final RelationName name;
        private final Object inverseValue;

        Key(RelationName name, Object inverseValue) {
            this.name = name;
            this.inverseValue = inverseValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            return inverseValue.equals(key.inverseValue) && name.equals(key.name);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + inverseValue.hashCode();
            return result;
        }
    }
}
