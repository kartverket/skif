package no.statkart.skif;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.util.CopyHelper;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class CopyHelperBubbleTest {
    @Test
    public void testReplaceSnapshotVersion() {
        TestBubbleId id1 = new TestBubbleId(123L, SnapshotVersion.CURRENT);
        TestBubbleId id2 = new TestBubbleId(456L, SnapshotVersion.CURRENT);

        TestBubble testBubble = new TestBubble();
        testBubble.setId(id1);
        testBubble.setOtherId(id2);

        TestBubble oldBubble = CopyHelper.copy(testBubble, SnapshotVersion.OLD);

        Assertions.assertThat(oldBubble.getId().getSnapshotVersion())
                .isSameAs(SnapshotVersion.OLD);
        Assertions.assertThat(oldBubble.getOtherId().getSnapshotVersion())
                .isSameAs(SnapshotVersion.OLD);

        TestBubble copyBubble = CopyHelper.copy(testBubble);

        Assertions.assertThat(copyBubble.getId().getSnapshotVersion())
                .isSameAs(SnapshotVersion.CURRENT);
        Assertions.assertThat(copyBubble.getOtherId().getSnapshotVersion())
                .isSameAs(SnapshotVersion.CURRENT);
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
