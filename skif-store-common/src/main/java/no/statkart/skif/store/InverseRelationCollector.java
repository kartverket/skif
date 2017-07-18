package no.statkart.skif.store;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Objekt som brukes til å samele inn referanser som inngår i inversrelasjoner. Null verdier og tommes sett samles ikke
 * inn fordi fravær av verdi håndteres ved sammenlikning mot innsamlede verder for objektet som
 * overskrives. Collectoren kan samle felter med enkelt releasjoner og sett relasjoner. I tillegg kan collectoren
 * samle inn relasjoner fra collections av like componenter. For disse brukes en egen objekttype {@code Values} som
 * samler samme relasjoner på tvers av slike component collections.
 */
public class InverseRelationCollector {
    private final Map<RelationName, Object> map = Maps.newHashMap();
    private boolean inCollection;

    /** Brukes for innsamling av releasjoner på tvers av collections av objekter med relasjoner */
    public static class Values implements Iterable {
        private final Set<Object> v = Sets.newHashSetWithExpectedSize(10);

        boolean add(Object object) {
            return v.add(object);
        }

        /**
         * @param objects som skal legges til
         * @return true hvis ingen av objektene fantes fra før
         */
        boolean addAll(Set<?> objects) {
            boolean allAdded = true;
            for (Object object : objects) {
                allAdded &= v.add(object);
            }
            return allAdded;
        }

        @Override
        public Iterator iterator() {
            return v.iterator();
        }
    }

    public void put(RelationName role, Object value) {
        if (value != null) {
            if (inCollection) {
                Object v = map.computeIfAbsent(role, k -> new Values());
                checkState(v instanceof Values, "Multiple felter i objektgraf mapper til samme rolle [%s]. Feltverdi [%s]", role, value);
                Values values = (Values) v;
                checkState(values.add(value), "Multiple felter/objekter i collection av objekter mapper til samme feltverdi [%s] for rolle [%s]", value, role);
            } else {
                checkState(map.put(role, value) == null, "Multiple felter i objektgraf mapper til samme rolle [%s]. Feltverdi [%s]", role, value);
            }
        }
    }

    public void put(RelationName role, Set<?> value) {
        if (value != null && !value.isEmpty()) {
            if (inCollection) {
                Object v = map.computeIfAbsent(role, k -> new Values());
                checkState(v instanceof Values, "Multiple felter i objektgraf mapper til samme rolle [%s]. Feltverdi [%s]", role, value);
                Values values = (Values) v;
                checkState(values.addAll(value), "Multiple felter/objekter (med Set-verdier) i en collection av objekter inneholder ikke disjunkte Set av relasjonsverdier. Rolle [%s], nye verdier [%s], eksisterende verdier[%s],   ", role, value, values.v);
            } else {
                checkState(map.put(role, value) == null, "Multiple felter i objekt mapper til samme rolle [%s]. Feltverdi [%s]", role, value);
            }
        }
    }

    public Set<Map.Entry<RelationName, Object>> entrySet() {
        return map.entrySet();
    }

    public void collectInverseRelationValues(InverseRelationParticipation object) {
        if (object != null) {
            object.collectInverseRelationValues(this);
        }
    }

    public void collectInverseRelationValues(Iterable<? extends InverseRelationParticipation> objects) {
        if (objects != null) {
            boolean oldValue = inCollection;
            try {
                // Kolleksjoner av objekter krever specialhåndtering, siden roller kan gentas på tvers av objekter i kolleksjonen,
                // men ikke innenfor et objekt.
                inCollection = true;
                for (InverseRelationParticipation object : objects) {
                    if (object != null) {
                        object.collectInverseRelationValues(this);
                    }
                }
            } finally {
                inCollection = oldValue;
            }
        }
    }
}
