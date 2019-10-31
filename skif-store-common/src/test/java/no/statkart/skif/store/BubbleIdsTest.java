package no.statkart.skif.store;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.assertj.core.util.Lists;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import static org.testng.Assert.*;

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

        TestBubbleId(Object value) {
            super(value, SnapshotVersion.CURRENT);
        }
    }


    @Test
    public void comparingBubbleIdValue() {
        final TestBubbleId bubbleId = new TestBubbleId(0L);
        assertThat(BubbleIds.comparingBubbleIdValue(bubbleId, bubbleId)).isEqualTo(0);
        assertThat(BubbleIds.comparingBubbleIdValue(bubbleId, null)).isLessThanOrEqualTo(-1);
        assertThat(BubbleIds.comparingBubbleIdValue(null, bubbleId)).isGreaterThanOrEqualTo(+1);

        final TestBubbleId bubbleId1 = new TestBubbleId(1L);
        final TestBubbleId bubbleId2 = new TestBubbleId(2L);

        assertThat(BubbleIds.comparingBubbleIdValue(bubbleId1, bubbleId1)).isEqualTo(0);
        assertThat(BubbleIds.comparingBubbleIdValue(bubbleId1, bubbleId2)).isLessThanOrEqualTo(-1);
        assertThat(BubbleIds.comparingBubbleIdValue(bubbleId2, bubbleId1)).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void comparingBubbleIdValue_ignores_snapshotVersion() {
        final TestBubbleId bubbleIdCurrent = new TestBubbleId(1L, SnapshotVersion.CURRENT);
        final TestBubbleId bubbleIdOld = new TestBubbleId(1L, SnapshotVersion.OLD);

        assertThat(BubbleIds.comparingBubbleIdValue(bubbleIdCurrent, bubbleIdCurrent)).isEqualTo(0);
        assertThat(BubbleIds.comparingBubbleIdValue(bubbleIdCurrent, bubbleIdOld)).isEqualTo(0);
        assertThat(BubbleIds.comparingBubbleIdValue(bubbleIdOld, bubbleIdCurrent)).isEqualTo(0);
    }

    @Test
    public void comparingBubbleIdValue_null_strings_go_last() {
        final TestBubbleId bubbleId1 = new TestBubbleId("null1");
        final TestBubbleId bubbleId2 = new TestBubbleId("null2");
        final TestBubbleId bubbleIdNull = new TestBubbleId(null);

        assertThat(BubbleIds.comparingBubbleIdValue(bubbleId1, bubbleId2)).isLessThanOrEqualTo(-1);
        assertThat(BubbleIds.comparingBubbleIdValue(bubbleId2, bubbleId1)).isGreaterThanOrEqualTo(1);
        assertThat(BubbleIds.comparingBubbleIdValue(bubbleId1, bubbleIdNull)).isLessThanOrEqualTo(-1);
        assertThat(BubbleIds.comparingBubbleIdValue(bubbleId2, bubbleIdNull)).isLessThanOrEqualTo(-1);

        final TreeSet<TestBubbleId> ids = new TreeSet<>(BubbleIds.comparingBubbleIdValue()); //delegerer ikke til BubbleId.equals
        ids.add(bubbleId1);
        ids.add(bubbleIdNull);
        ids.add(bubbleId2);

        assertThat(ids).containsExactly(bubbleId1, bubbleId2, bubbleIdNull);
    }

    /**
     * Demonstrerer bruk av AssertJ API
     */
    @Test
    public void comparingBubbleIdValue_with_AssertJ() {
        final TestBubbleId bubbleIdCurrent = new TestBubbleId(1L, SnapshotVersion.CURRENT);
        final TestBubbleId bubbleIdOld = new TestBubbleId(1L, SnapshotVersion.OLD);

        assertThat(bubbleIdCurrent)
                .isNotEqualTo(bubbleIdOld)
                .usingComparator(BubbleIds.comparingBubbleIdValue())
                .isEqualTo(bubbleIdOld);

        assertThat(ImmutableSet.of(bubbleIdCurrent))
                .doesNotContain(bubbleIdOld)
                .usingElementComparator(BubbleIds.comparingBubbleIdValue())
                .contains(bubbleIdOld);

        assertThat(ImmutableList.of(bubbleIdCurrent, bubbleIdOld))
                .doesNotHaveDuplicates();
    }

    @Test
    public void valueEquals() {
        final TestBubbleId bubbleId = new TestBubbleId(0L);

        assertThat(BubbleIds.valueEquals(null, null)).isFalse();
        assertThat(BubbleIds.valueEquals(null, bubbleId)).isFalse();
        assertThat(BubbleIds.valueEquals(bubbleId, null)).isFalse();
        assertThat(BubbleIds.valueEquals(bubbleId, bubbleId)).isTrue();

        assertThat(BubbleIds.valueEquals(bubbleId, new TestBubbleId(1L))).isFalse();

        assertThat(BubbleIds.valueEquals(new TestBubbleId(1L, SnapshotVersion.CURRENT), new TestBubbleId(1L, SnapshotVersion.CURRENT))).isTrue();
        assertThat(BubbleIds.valueEquals(new TestBubbleId(1L, SnapshotVersion.CURRENT), new TestBubbleId(1L, SnapshotVersion.OLD))).isTrue();
        assertThat(BubbleIds.valueEquals(new TestBubbleId(1L, SnapshotVersion.OLD), new TestBubbleId(1L, SnapshotVersion.CURRENT))).isTrue();
    }
}