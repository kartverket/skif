package no.statkart.skif.skiftest.domain;

import java.util.Map;
import java.util.Set;

/**
 * Klasse med Map.
 */
public class M {
    private Map<String, Set<A>> mapOfAs;

    public Map<String, Set<A>> getMapOfAs() {
        return mapOfAs;
    }

    public void setMapOfA(Map<String, Set<A>> mapOfAs) {
        this.mapOfAs = mapOfAs;
    }
}
