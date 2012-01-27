package no.statkart.skif.mockup;

import no.statkart.skif.store.BubbleObject;

import java.util.Collections;
import java.util.Set;

/**
 * TODO: Erstatt med ordentlig transfer
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class MockupTransfer {
    private final Set<BubbleObject> inserts;
    private final Set<BubbleObject> updates;
    private final Set<BubbleObject> deletes;

    public MockupTransfer(Set<BubbleObject> inserts, Set<BubbleObject> updates, Set<BubbleObject> deletes) {
        this.inserts = Collections.unmodifiableSet(inserts);
        this.updates = Collections.unmodifiableSet(updates);
        this.deletes = Collections.unmodifiableSet(deletes);
    }

    public Set<BubbleObject> getInserts() {
        return inserts;
    }

    public Set<BubbleObject> getUpdates() {
        return updates;
    }

    public Set<BubbleObject> getDeletes() {
        return deletes;
    }
}
