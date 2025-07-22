package no.statkart.skif;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.util.CopyHelper;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CopyHelperBubbleTest {
    @Test
    public void testReplaceSnapshotVersion() {
        TestBubbleId id1 = new TestBubbleId(123L, SnapshotVersion.CURRENT);
        TestBubbleId id2 = new TestBubbleId(456L, SnapshotVersion.CURRENT);

        TestBubble testBubble = new TestBubble();
        testBubble.setId(id1);
        testBubble.setOtherId(id2);

        TestBubble oldBubble = CopyHelper.copy(testBubble, SnapshotVersion.OLD);
        assertThat(oldBubble.getId().getSnapshotVersion()).isSameAs(SnapshotVersion.OLD);
        assertThat(oldBubble.getOtherId().getSnapshotVersion()).isSameAs(SnapshotVersion.OLD);

        TestBubble copyBubble = CopyHelper.copy(testBubble);
        assertThat(copyBubble.getId().getSnapshotVersion()).isSameAs(SnapshotVersion.CURRENT);
        assertThat(copyBubble.getOtherId().getSnapshotVersion()).isSameAs(SnapshotVersion.CURRENT);
    }

    @Test
    void snapshotVersion_null_defaults_to_serialized_serializedSapshotVersion() {
        TestBubbleId id1 = new TestBubbleId(123L, SnapshotVersion.createInstance("3333-01-01 00:00:00.0"));
        TestBubbleId id2 = new TestBubbleId(456L, SnapshotVersion.createInstance("3333-01-01 00:00:00.0"));

        TestBubble testBubble = new TestBubble();
        testBubble.setId(id1);
        testBubble.setOtherId(id2);

        {
            TestBubble defaultCopy = CopyHelper.copy(testBubble, null);
            assertThat(defaultCopy.getId().getSnapshotVersion()).isEqualTo(SnapshotVersion.createInstance("3333-01-01 00:00:00.0"));
            assertThat(defaultCopy.getOtherId().getSnapshotVersion()).isEqualTo(SnapshotVersion.createInstance("3333-01-01 00:00:00.0"));
        }
        {
            TestBubble defaultCopy = CopyHelper.copy(testBubble);
            assertThat(defaultCopy.getId().getSnapshotVersion()).isEqualTo(SnapshotVersion.createInstance("3333-01-01 00:00:00.0"));
            assertThat(defaultCopy.getOtherId().getSnapshotVersion()).isEqualTo(SnapshotVersion.createInstance("3333-01-01 00:00:00.0"));
        }

        {
            TestBubble specifiedCopy = CopyHelper.copy(testBubble, SnapshotVersion.createInstance("2222-01-01 00:00:00.0"));
            assertThat(specifiedCopy.getId().getSnapshotVersion()).isEqualTo(SnapshotVersion.createInstance("2222-01-01 00:00:00.0"));
            assertThat(specifiedCopy.getOtherId().getSnapshotVersion()).isEqualTo(SnapshotVersion.createInstance("2222-01-01 00:00:00.0"));
        }
    }

    public static class TestBubble extends AbstractBubbleObject {
        private static final long serialVersionUID = 1;

        private TestBubbleId otherId;

        public TestBubbleId getOtherId() {
            return otherId;
        }

        public void setOtherId(TestBubbleId otherId) {
            this.otherId = otherId;
        }
    }

    public static class TestBubbleId extends AbstractBubbleId<TestBubble> {
        private static final long serialVersionUID = 1;

        public TestBubbleId(Long value, SnapshotVersion version) {
            super(value, version);
        }

        @Override
        public Long getValue() {
            return (Long) super.getValue();
        }
    }
}
