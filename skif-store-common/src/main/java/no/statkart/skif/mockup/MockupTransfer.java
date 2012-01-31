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
    private Set<BubbleObject> inserts;
    private Set<BubbleObject> updates;
    private Set<BubbleObject> deletes;
    private int testNumber;

    public MockupTransfer() {
    }

    public MockupTransfer(Set<BubbleObject> inserts, Set<BubbleObject> updates, Set<BubbleObject> deletes, int testNumber) {
        this.testNumber = testNumber;
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

    public void setInserts(Set<BubbleObject> inserts) {
        this.inserts = inserts;
    }

    public void setUpdates(Set<BubbleObject> updates) {
        this.updates = updates;
    }

    public void setDeletes(Set<BubbleObject> deletes) {
        this.deletes = deletes;
    }

    public int getTestNumber() {
        return testNumber;
    }
}
