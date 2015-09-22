package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.relation.cache.annotation.Relation;
import no.statkart.skif.store.relation.cache.annotation.RelationType;
import no.statkart.skif.util.CopyHelper;

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
    final static int MAX_LEVELS = 4;
    private boolean[] enabled = new boolean[MAX_LEVELS];

    public boolean isEnabled(int level) {
        return enabled[level];
    }

    /**
     * Enabler og disabler relation caching. Ved disabling evictes alle cachet relasjoner.
     */
    public void setEnabled(int level, boolean enabled) {
        checkState(level==0 || this.enabled[level-1]==false || enabled, "Disabling av relation caching i current UnitOfWork støttes ikke når underliggende UnitOfWork eller session har caching enablet");
        if (enabled==false && this.enabled[level]==true) {
            evictAll();
        }
        this.enabled[level] = enabled;
    }

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

    public void onBeginUnitOfWork(int level) {
        enabled[level] = enabled[level-1];
    }

    public void onCommitUnitOfWork(int level) {
        if (enabled[level] && !enabled[level-1]) {
            evictAll();
        }
        for (Map.Entry<Key, RelationEntry> mapElement : inverseRelationMap.entrySet()) {
            RelationEntry entry = mapElement.getValue();
            entry.commitEntry(level);
        }
    }

    public void onAbortUnitOfWork(int level) {
        if (enabled[level] && !enabled[level-1]) {
            evictAll();
        }
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
                if (!inverseRelationEntry.isMaterialized(level)) {
                    missingInverseValues.add(inverseValue);
                }
            }
        }
        return missingInverseValues;
    }

    public void evictAll() {
        // TODO: Dersom relasjonscachen inneholder endringer blir nedenstående feil. Løsningen må utvides til alltid å ta vare på endringene. Krever endringer mer omfattende endringer i RelationTracker så det tas senere
        inverseRelationMap.clear();
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

    /**
     * Denne klasse holder på styr på hvilke objekter som inngår i en invers relasjon for gitt unit-of-work, relasjon og
     * verdi (bubbleId eller verdi). Klassen anvender et array av {@code RelationTracker}s hvor index i array
     * svarer til unit-of-work level.
     *
     *
     * @author Henrik Fredholm
     * @since 2.4
     */
    private static class RelationEntry {
        final static int MAX_LEVELS = 4;
        final RelationTracker[] relations = new RelationTracker[MAX_LEVELS];

        public void addId(int level, BubbleId<?> sourceId) {
            getOrCreateRelation(level).add(sourceId);
        }

        private RelationTracker getOrCreateRelation(int level) {
            RelationTracker relation = relations[level];
            if (relation == null) {
                relation = relations[level] = new RelationTracker();
            }
            return relation;
        }


        public void removeId(int level, BubbleId<?> sourceId) {
            getOrCreateRelation(level).remove(sourceId);
        }


        public boolean isMaterialized(int level) {
            boolean materialized = false;
            for (int i = level; i >= 0; --i) {
                if (isMaterializedAt(i)) {
                    materialized = true;
                }
            }
            return materialized;
        }

        private boolean isMaterializedAt(int level) {
            return relations[level] != null && relations[level].isMaterialised();
        }

        /**
         * Henter ut relasjonsverdien for et gitt {@coce level}. RelationEntry forventes å inneholder  et antall
         * RelationTrackers for levels {@code 'i' <= level} hvor minst en av disse trackers allerede vil være materalisert.
         * RelationTrackers for høyere levels enn den som er materialisert kan være uinitialiserte eller inneholde
         * endringsoperasjon som skal appliseres for å få riktig relasjonsverdi for et gitt {@code level}. Disse vil
         * bli matrialisert opp til om med {@code level} (dersom de finnes) ifm beregning av relasjonsverdien.
         */
        public Object getRelationValue(int level) {
            // Finn level 'i' som inneholder info om relasjon startende fra 'level'
            int i = level;
            while ((i >= 0) && relations[i] == null) {
                i--;
            }
            if (i == -1) {
                throw new ImplementationException(String.format("Forventet å finne en eller flere RelationTrackers i RelationEntry\nLevel=%d\nrelations[3]=%s\nrelations[2]=%s\nrelations[1]=%s\nrelations[0]=%s\n", level, relations[3], relations[2], relations[1], relations[0]));
            }
            // Hvis RelationTracker ikke er materalisert for inneværende nivå 'i', må vi hente verdien fra et underliggende nivå. NB: Det kan være huller.
            int j = i;
            while (j >= 0 && (relations[j]==null || !relations[j].isMaterialised())) {
                j--;
            }
            if (j == -1) {
                throw new ImplementationException(String.format("Forventet å finne en materialisert RelationTracker i RelationEntry.\nLevel=%d\nrelations[3]=%s\nrelations[2]=%s\nrelations[1]=%s\nrelations[0]=%s\n", level, relations[3], relations[2], relations[1], relations[0]));
            }
            // j er materialisert, og skal brukes som startpunkt for videre materialisering opp til level 'i'
            while (j < i) {
                Object value = CopyHelper.copy(relations[j].getRelation());
                if (relations[j+1]==null) {
                    relations[j+1] = new RelationTracker(true, value);
                } else {
                    relations[j + 1].materialise(value);
                }
                j++;
            }
            return relations[i].getRelation();
        }

        public Object setRelationValue(int level, Object relationValue) {
            for (int i = 0; i < level; i++) {
                if (relations[i] != null) {
                    relationValue = relations[i].applyOperations(relationValue);
                }
            }
            if (relations[level] != null) {
                relations[level].materialise(relationValue);
            } else {
                relations[level] = new RelationTracker(true, relationValue);
            }
            return relations[level].getRelation();

        }

        public void commitEntry(int level) {
            if (level > 0) {
                if (relations[level] != null) {
                    if (relations[level - 1] == null) {
                        relations[level - 1] = relations[level];
                    } else {
                        relations[level].commitInto(relations[level - 1]);
                    }
                }
            }
            relations[level] = null;
        }

        public void abortEntry(int level) {
            relations[level] = null;
        }

        public boolean hasNoRelationsInRemainigLevels(int level) {
            for (int i = level; i >= 0; i--) {
                if (relations[i] != null) return false;
            }
            return true;
        }
    }
}
