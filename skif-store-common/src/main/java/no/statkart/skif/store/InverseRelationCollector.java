package no.statkart.skif.store;

import com.google.common.collect.Maps;
import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Objekt som brukes til å samele inn referanser som inngår i inversrelasjoner. Null verdier og tommes sett samles ikke
 * inn fordi fravær av verdi håndteres fremkommer av seg selv ved sammenlikning mot innsamlede verder for objektet som
 * overskrives.
 */
public class InverseRelationCollector {
    private final Map<RelationName, Object> map = Maps.newHashMap();

    public void put(RelationName role, Object value) {
        if (value != null) {
            checkState(map.put(role, value) == null, "Multiple felter i objekt mapper til samme rolle [%s]. Feltverdi [%s]", role, value);
        }
    }

    public void put(RelationName role, Set<? extends BubbleId<?>> value) {
        if (value != null && !value.isEmpty()) {
            checkState(map.put(role, value) == null, "Multiple felter i objekt mapper til samme rolle [%s]. Feltverdi [%s]", role, value);
        }
    }

    public Set<Map.Entry<RelationName, Object>> entrySet() {
        return map.entrySet();
    }

    public void collectInverseRelationValues(InverseRelationParticipation object) {
        if (object!=null) {
           object.collectInverseRelationValues(this);
        }
    }

    public void collectInverseRelationValues(Iterable<? extends InverseRelationParticipation> objects) {
        if (objects!=null) {
            for (InverseRelationParticipation object : objects) {
                if (object!=null) {
                    object.collectInverseRelationValues(this);
                }
            }
        }
    }
}
