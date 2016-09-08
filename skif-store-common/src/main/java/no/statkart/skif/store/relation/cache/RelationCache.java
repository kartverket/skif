package no.statkart.skif.store.relation.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.StoreEntry;
import no.statkart.skif.store.relation.cache.annotation.Relation;
import no.statkart.skif.store.relation.cache.annotation.RelationType;
import no.statkart.skif.util.CopyHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Klasse for caching av invers relasjoner i Store. Inversrelasjonen kan enten være fra en bobleId eller et
 * vilkårlig valueobjekt og går til boblen som har denne verdien. Dersom verdien ligger i en komponent av boblen går
 * inversrelasjonen alltid til boblen som eier komponenten.
 * <p>
 * Eiende boble må fortelle relasjonscachen hver gang relasjonen endres. Dette skjer via kall til
 * {@link #onChangeRelation} hvor kan det angis hva den gamle verdi var og hva den nye verdi er. Hvis relasjonen har
 * {@code mange} kardinalitet så brukes {@code null} for gammel verdi ved {@code add} og {@code null} for ny verdier ved
 * {@code remove}. Relasjonscachen er således alltid i synk med hensyn til hvilke objekter som er lagt til og fjernet.
 * <p>
 * Relasjonen som caches trenger ikke å være materialisert for at cachen kan holde styr på hvilke verdier som har blitt
 * lagt til eller fjernet. Relasjonen materialiseres først hvis relasjonens verdier skal  brukes. Da appliseres
 * alle cachet endringene på den materialiserte relasjonen slik at relasjonen fremstå som endret og med riktig innhold.
 * <p>
 * For å kunne forenkle håndtering av relasjonscaching av identer som beregnes på basis av flere objekter, så behandler
 * relasjonscachen relasjoner til valueobjekter litt anderledes enn relasjner til bobleId. For valueobjekter så husker
 * relasjonscachen selv den gamle verdien slik at den ikke skal oppgis. Derved trenger boblen ikke å være i stand
 * til å kunne beregne den gamle verdien eller eksplisitt å hente ut den gamle verdien før objekter som inngår i beregningen
 * endres. Antagelsen for valueobjekter er at relasjonen har kardinalitet 1 og at den gamle verdien ikke lengre skal
 * peke på objektet.
 * <p/>
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class RelationCache {
    final private Map<Key, RelationEntry> inverseRelationMap = Maps.newHashMap();
    final private Map<BubbleId<?>, List<CachedInverseValueEntry>> sourceIdToInverseValueMap = Maps.newHashMap();
    final private static int MAX_LEVELS = StoreEntry.MAX_LEVELS;
    final private boolean[] enabled = new boolean[MAX_LEVELS];

    public boolean isEnabled(int level) {
        return enabled[level];
    }

    /**
     * Enabler og disabler relation caching. Ved disabling evictes alle cachet relasjoner.
     */
    public void setEnabled(int level, boolean enabled) {
        if (this.enabled[level]) {
            evictAll();
        }
        this.enabled[level] = enabled;
    }

    private CachedInverseValueEntry getCachedInverseValueEntry(RelationName relationName, BubbleId<?> sourceId) {
        List<CachedInverseValueEntry> cachedInverseValueEntries = sourceIdToInverseValueMap.get(sourceId);
        if (cachedInverseValueEntries==null) return null;
        for (CachedInverseValueEntry cachedInverseValueEntry : cachedInverseValueEntries) {
            if (cachedInverseValueEntry.getRelationName().equals(relationName)) {
                return cachedInverseValueEntry;
            }
        }
        return null;
    }

    private CachedInverseValueEntry getOrCreateCachedInverseValueEntry(RelationName relationName, BubbleId<?> sourceId) {
        List<CachedInverseValueEntry> cachedInverseValueEntries = sourceIdToInverseValueMap.get(sourceId);
        if (cachedInverseValueEntries==null) {
            cachedInverseValueEntries = Lists.newArrayListWithCapacity(1);
            sourceIdToInverseValueMap.put(sourceId, cachedInverseValueEntries);
        }
        for (CachedInverseValueEntry cachedInverseValueEntry : cachedInverseValueEntries) {
            if (cachedInverseValueEntry.getRelationName().equals(relationName)) {
                return cachedInverseValueEntry;
            }
        }
        CachedInverseValueEntry cachedInverseValueEntry = new CachedInverseValueEntry(relationName);
        cachedInverseValueEntries.add(cachedInverseValueEntry);
        return cachedInverseValueEntry;
    }

    public <E> void onChangeRelation(int level, boolean inAttachedMode, RelationName relationName, BubbleId<?> sourceId, @Nullable E oldInverseValue, @Nullable E newInverseValue) {
        if (oldInverseValue == null || !(oldInverseValue instanceof BubbleId)) {
            // For value objekt relasjoner skal gammel verdi alltid hentes fra cachen
            oldInverseValue = lookupCachedInverseValueAndMarkAsRemoved(level, relationName, sourceId);
        }
        removeId(level, inAttachedMode, relationName, oldInverseValue, sourceId);
        addId(level, inAttachedMode, relationName, newInverseValue, sourceId);
        if (newInverseValue != null && !(newInverseValue instanceof BubbleId)) {
            saveCachedInverseValue(level, relationName, sourceId, newInverseValue);
        }
    }

    private Key createCurrentValueKey(RelationName relationName, BubbleId<?> sourceId) {
        return new Key(relationName, sourceId.getValue());
    }

    /**
     * Kalles når en value relation materialiseres. Metoden finner ut av om relasjonen er endret for noen av de sourceIds
     * som ble funnet for inverseValue. De sourceIds hvor relasojnen er endret fjernes fra returnverdien, men de
     * som har uendret relasjon får cachet informasjon om at sourceId er knyttet til inverseValue slik at den gamle
     * verdien vil være tilgjengelig hvis relasjonen endres.
     */
    private <E> Object resolveAndSaveCurrentInverseValueForSourceIdsIfUnchanged(int level, RelationName relationName, E inverseValue, Object relationValue) {
        if (relationValue instanceof Set) {
            final Set resolvedValues = Sets.newHashSetWithExpectedSize(((Set) relationValue).size());
            for (BubbleId<?> sourceId : (Set<BubbleId<?>>) relationValue) {
                final CachedInverseValueEntry cachedInverseValueEntry = getOrCreateCachedInverseValueEntry(relationName, sourceId);
                if (cachedInverseValueEntry.getValue(level) == null) {
                    cachedInverseValueEntry.setValue(level, inverseValue);
                    resolvedValues.add(sourceId);
                }
            }
            return resolvedValues;
        } else if (relationValue != null) {
            final BubbleId<?> sourceId = (BubbleId<?>) relationValue;
            final CachedInverseValueEntry cachedInverseValueEntry = getOrCreateCachedInverseValueEntry(relationName, sourceId);
            if (cachedInverseValueEntry.getValue(level) == null) {
                cachedInverseValueEntry.setValue(level, inverseValue);
                return relationValue;
            }
        }
        return null;
    }

    private <E> void saveCachedInverseValue(int level, RelationName relationName, BubbleId<?> sourceId, E newInverseValue) {
        CachedInverseValueEntry cachedInverseValueEntry = getOrCreateCachedInverseValueEntry(relationName, sourceId);
        cachedInverseValueEntry.setValue(level, newInverseValue);
    }


    private <E> E lookupCachedInverseValueAndMarkAsRemoved(int level, RelationName relationName, BubbleId<?> sourceId) {
        CachedInverseValueEntry cachedInverseValueEntry = getCachedInverseValueEntry(relationName, sourceId);
        if (cachedInverseValueEntry != null) {
            @SuppressWarnings("unchecked")
            E value = (E) cachedInverseValueEntry.getValue(level);
            cachedInverseValueEntry.markRemoved(level);
            return value;
        } else {
            return null;
        }
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

    /**
     * Hjelpemetode for testing som ikke ellers bør brukes.
     */
    public <E> RelationTracker peekRelationTracker(int level, RelationName relationName, E inverseValue) {
        RelationEntry inverseRelationEntry = getInverseRelation(relationName, inverseValue, false);
        if (inverseRelationEntry != null) {
            return CopyHelper.copy(inverseRelationEntry.relations[level]); // defensive copy
        } else {
            return null;
        }
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
        checkNotNull(inverseValue, "Uventet null verdi for relation: %s", relationName);
        RelationEntry inverseRelationEntry = getInverseRelation(relationName, inverseValue, true);
        if (!(inverseValue instanceof BubbleId)) {
            relationValue = resolveAndSaveCurrentInverseValueForSourceIdsIfUnchanged(level, relationName, inverseValue, relationValue);
        }
        return inverseRelationEntry.setRelationValue(level, relationValue);
    }


    public RelationName getRelationName(Method method) {
        //TODO optimaliser via singleton lookup for method. Dette endre seg ikke og kan derfor caches på tvers av alle sessions
        RelationName name = null;
        Relation annotation = method.getAnnotation(Relation.class);
        if (annotation != null) {
            checkState(annotation.type() == RelationType.INVERSE);
            Class<? extends Enum> enumClass = SkifUtil.classForName(method.getDeclaringClass().getCanonicalName() + "$Role");
            name = (RelationName) Enum.valueOf(enumClass, annotation.name());
        }
        return name;
    }

    public void onBeginUnitOfWork(int level) {
        enabled[level] = enabled[level - 1];
    }

    public void onCommitUnitOfWork(int level) {
        if (enabled[level] && !enabled[level - 1]) {
            evictAll();
        }
        for (Map.Entry<Key, RelationEntry> mapElement : inverseRelationMap.entrySet()) {
            RelationEntry entry = mapElement.getValue();
            entry.commitEntry(level);
        }
        Iterator<List<CachedInverseValueEntry>> iterator = sourceIdToInverseValueMap.values().iterator();
        while (iterator.hasNext()) {
            List<CachedInverseValueEntry> next = iterator.next();
            Iterator<CachedInverseValueEntry> iteratorInner = next.iterator();
            while(iteratorInner.hasNext())  {
                CachedInverseValueEntry nextInner = iteratorInner.next();
                nextInner.commit(level);
                if (nextInner.canBeRemoved(level)) {
                    iteratorInner.remove();
                }
            }
            if (next.isEmpty()) {
                iterator.remove();
            }
        }
    }

    public void onAbortUnitOfWork(int level) {
        if (enabled[level] && !enabled[level - 1]) {
            evictAll();
        }
        Iterator<Map.Entry<Key, RelationEntry>> relationEntryIterator = inverseRelationMap.entrySet().iterator();
        while (relationEntryIterator.hasNext()) {
            Map.Entry<Key, RelationEntry> mapElement = relationEntryIterator.next();
            RelationEntry entry = mapElement.getValue();
            entry.abortEntry(level);
            if (entry.hasNoRelationsInRemainigLevels(level - 1)) {
                relationEntryIterator.remove();
            }
        }
        Iterator<List<CachedInverseValueEntry>> iterator = sourceIdToInverseValueMap.values().iterator();
        while (iterator.hasNext()) {
            List<CachedInverseValueEntry> next = iterator.next();
            Iterator<CachedInverseValueEntry> iteratorInner = next.iterator();
            while(iteratorInner.hasNext())  {
                CachedInverseValueEntry nextInner = iteratorInner.next();
                nextInner.abort(level);
                if (nextInner.canBeRemoved(level)) {
                    iteratorInner.remove();
                }
            }
            if (next.isEmpty()) {
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

    public <E> boolean isMaterialized(int level, RelationName relationName, E inverseValue) {
        RelationEntry inverseRelationEntry = getInverseRelation(relationName, inverseValue, false);
        if (inverseRelationEntry != null) {
            return inverseRelationEntry.isMaterialized(level);
        } else {
            return false;
        }
    }

    public void evictAll() {
        inverseRelationMap.clear();
        sourceIdToInverseValueMap.clear();
    }

    public Map<Key, RelationEntry> filterOnKeys(Comparable<Key> keyComparable) {
        Map<Key, RelationEntry> filtered = Maps.newHashMap();
        for (Map.Entry<Key, RelationEntry> entry : inverseRelationMap.entrySet()) {
            if (keyComparable.compareTo(entry.getKey()) == 0) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }

    public Map<Key, RelationEntry> filterOnRelationName(String relationName) {
        return filterOnKeys(relationNameEquals(relationName));
    }

    private static Comparable<Key> relationNameEquals(final String relationName) {
        return new Comparable<Key>() {
            @Override
            public int compareTo(Key o) {
                return relationName.compareTo(o.name.toString());
            }
        };
    }

    public Set<String> getCachedRelationNames() {
        Set<String> relationNames = Sets.newHashSet();
        for (Key key : inverseRelationMap.keySet()) {
            relationNames.add(key.name.toString());
        }
        return relationNames;
    }

    public void onSourceIdRemoved(int level, BubbleId<?> sourceId) {
        List<CachedInverseValueEntry> cachedInverseValueEntries = sourceIdToInverseValueMap.get(sourceId);
        if (cachedInverseValueEntries!=null) {
            for (CachedInverseValueEntry entry : cachedInverseValueEntries) {
                Object inverseValue = entry.getValue(level);
                if (inverseValue!=null) {
                    onChangeRelation(level, true, entry.getRelationName(), sourceId, inverseValue,null);
                }
            }
        }
    }

    public static class Key {
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

        @Override
        public String toString() {
            return name + ":" + inverseValue;
        }
    }

    /**
     * Denne klasse holder på styr på hvilke objekter som inngår i en invers relasjon for gitt unit-of-work, relasjon og
     * verdi (bubbleId eller verdi). Klassen anvender et array av {@code RelationTracker}s hvor index i array
     * svarer til unit-of-work level.
     *
     * @author Henrik Fredholm
     * @since 2.4
     */
    private static class RelationEntry {
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
         * Henter ut relasjonsverdien for et gitt {@code level}. RelationEntry forventes å inneholder  et antall
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
            while (j >= 0 && (relations[j] == null || !relations[j].isMaterialised())) {
                j--;
            }
            if (j == -1) {
                throw new ImplementationException(String.format("Forventet å finne en materialisert RelationTracker i RelationEntry.\nLevel=%d\nrelations[3]=%s\nrelations[2]=%s\nrelations[1]=%s\nrelations[0]=%s\n", level, relations[3], relations[2], relations[1], relations[0]));
            }
            // j er materialisert, og skal brukes som startpunkt for videre materialisering opp til level 'i'
            while (j < i) {
                Object value = CopyHelper.copy(relations[j].getRelation());
                if (relations[j + 1] == null) {
                    relations[j + 1] = new RelationTracker(true, value);
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

        @Override
        public String toString() {
            return "RelationEntry{" +
                    "relations=" + Arrays.toString(relations) +
                    '}';
        }
    }

    private static class CachedInverseValueEntry {
        final private RelationName relationName;
        final private Object[] values = new Object[MAX_LEVELS];
        final private Object REMOVED_MARKER = new Object();

        private CachedInverseValueEntry(RelationName relationName) {
            this.relationName = relationName;
        }

        private RelationName getRelationName() {
            return relationName;
        }

        public Object getValue(int level) {
            for (int i = level; i >= 0; i--)
                if (values[i] != null) {
                    return values[i] == REMOVED_MARKER ? null : values[i];
                }
            return null;
        }

        public void markRemoved(int level) {
            values[level] = REMOVED_MARKER;
        }

        public void setValue(int level, Object value) {
            values[level] = value;
        }

        public boolean canBeRemoved(int level) {
            for (int i = 0; i <= level; i++) {
                if (values[i] != null) {
                    return false;
                }
            }
            return true;
        }

        public void commit(int level) {
            if (level - 1 >= 0) {
                values[level - 1] = values[level];
            }
            values[level] = null;
        }

        public void abort(int level) {
            values[level] = null;
        }
    }
}
