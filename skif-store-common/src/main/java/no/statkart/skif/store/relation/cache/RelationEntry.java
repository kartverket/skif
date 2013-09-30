package no.statkart.skif.store.relation.cache;

import no.statkart.skif.store.BubbleId;

import java.util.*;

/**
 * Denne klasse holder på styr på hvilke objekter som inngår i en invers relasjon for gitt unit-of-work, relasjon og objekt.
 * Klassen anvender et array av {@code RelationTracker}s hvor index i array svarer til unit-of-work level.
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

    public Set getCachedIds(int level) {
        int i = level;
        while ((i >= 0) && relations[i] == null) {
            i--;
        }
        if (i == -1) {
            return null;
        } else {
            int j = i;
            while ((j >= 0) && !relations[j].isMaterialised()) {
                j--;
            }
            // j er materialisert, og skal brukes som startpunkt for videre materialisering
            while (j < i) {
                relations[j + 1].materialise(new HashSet(relations[j].getValuesAsIds()));
            }
            return relations[i].getValuesAsIds();
        }
    }

    public Set setChachedIds(int level, Set ids) {
        for (int i = 0; i < level; i++) {
            if (relations[i] != null) {
                relations[i].materialiseInto(ids);
            }
        }
        if (relations[level] != null) {
            relations[level].materialise(ids);
        } else {
            relations[level] = new RelationTracker(true, ids);
        }
        return relations[level].getValuesAsIds();

    }

    public void commitEntry(int level) {
        if (level > 0) {
            if (relations[level] != null) {
                if (relations[level-1]==null)  {
                    relations[level - 1] = relations[level];
                } else  {
                    relations[level].materialiseInto(relations[level-1]);
                }
            }
        }
        relations[level] = null;
    }

    public void abortEntry(int level) {
        relations[level] = null;
    }

    public boolean hasNoRelationsInRemainigLevels(int level) {
        for(int i=level; i>=0;i--) {
            if (relations[i]!= null) return false;
        }
        return true;
    }
}
