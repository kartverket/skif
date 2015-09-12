package no.statkart.skif.store.relation.cache;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.util.CopyHelper;

/**
 * Denne klasse holder på styr på hvilke objekter som inngår i en invers relasjon for gitt unit-of-work, relasjon og
 * verdi (bubbleId eller verdi). Klassen anvender et array av {@code RelationTracker}s hvor index i array
 * svarer til unit-of-work level.
 *
 * TODO: Denne klasse kan være privat i RelationCache da det er det eneste sted den skal brukes
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class RelationEntry {
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
