package no.statkart.skif.mockup;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.BubbleTransfer;
import no.statkart.skif.store.UnitOfWorkTransfer;

import java.util.*;

/**
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.1
 */
public class MockupTransfer extends UnitOfWorkTransfer {
    private TestNumber testNumber;

    @Deprecated // WS-mapping
    public MockupTransfer() {
    }

    public MockupTransfer(List<? extends BubbleObject> inserts, List<? extends BubbleObject> updatedObjects, List<? extends BubbleObject> deletedObjects, TestNumber testNumber) {
        super(inserts, updatedObjects, deletedObjects);
        this.testNumber = testNumber;
    }

    public TestNumber getTestNumber() {
        return testNumber;
    }

    @Deprecated // WS-mapping
    public void setTestNumber(TestNumber testNumber) {
        this.testNumber = testNumber;
    }
}
