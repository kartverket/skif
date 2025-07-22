package no.statkart.skif.store;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class BubbleIdsTest {
    @Test
    public void testEqualsIgnoreSnapshotVersion() {
        TestBubbleId id1a1 = new TestBubbleId(1L, SnapshotVersion.CURRENT);
        TestBubbleId id1a2 = new TestBubbleId(1L, SnapshotVersion.CURRENT);
        TestBubbleId id1b1 = new TestBubbleId(1L, SnapshotVersion.OLD);
        TestBubbleId id1b2 = new TestBubbleId(1L, SnapshotVersion.OLD);
        TestBubbleId id2a = new TestBubbleId(2L, SnapshotVersion.CURRENT);
        TestBubbleId id2b = new TestBubbleId(2L, SnapshotVersion.OLD);

        assertTrue(BubbleIds.equalsIgnoreSnapshotVersion(null, null));
        assertTrue(BubbleIds.equalsIgnoreSnapshotVersion(id1a1, id1a1));
        assertTrue(BubbleIds.equalsIgnoreSnapshotVersion(id1a1, id1a2));
        assertTrue(BubbleIds.equalsIgnoreSnapshotVersion(id1b1, id1b1));
        assertTrue(BubbleIds.equalsIgnoreSnapshotVersion(id1b1, id1b2));
        assertTrue(BubbleIds.equalsIgnoreSnapshotVersion(id1a1, id1b1));
        assertTrue(BubbleIds.equalsIgnoreSnapshotVersion(id1a2, id1b2));
        assertTrue(BubbleIds.equalsIgnoreSnapshotVersion(id2a, id2b));

        assertFalse(BubbleIds.equalsIgnoreSnapshotVersion(null, id1a1));
        assertFalse(BubbleIds.equalsIgnoreSnapshotVersion(id1a1, null));
        assertFalse(BubbleIds.equalsIgnoreSnapshotVersion(id1a1, id2a));
        assertFalse(BubbleIds.equalsIgnoreSnapshotVersion(id1b1, id2b));
        assertFalse(BubbleIds.equalsIgnoreSnapshotVersion(id1a1, id2b));
    }

    private static class TestBubbleId extends AbstractBubbleId<BubbleObject> {
        private static final long serialVersionUID = 1L;

        TestBubbleId(Object value, SnapshotVersion version) {
            super(value, version);
        }
    }
}
