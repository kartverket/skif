package no.statkart.skif;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.util.CopyHelper;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RecursiveAssertjComparisonTest {

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

        public TestBubbleId(Object value) {
            super(value);
        }

        @Override
        public Long getValue() {
            return (Long) super.getValue();
        }
    }

    /**
     * Verifiserer at {@link AbstractBubbleObject} og {@link AbstractBubbleId} lar seg sammenligne med
     * standard oppsett av rekursiv sammenligning i AssertJ.
     */
    @Test
    void recursiveComparisonTest() {
        TestBubble testBubbleA = new TestBubble();
        testBubbleA.setOtherId(new TestBubbleId(1L));

        //trigger mutabel state av otherId.typeInfo
        assertThat(testBubbleA.getOtherId().getValueType()).isEqualTo(Long.class);

        TestBubble testBubbleB = new TestBubble();
        testBubbleB.setOtherId(new TestBubbleId(1L));
        assertThat(testBubbleB).usingRecursiveComparison().isEqualTo(testBubbleA);

        TestBubble copyBubble = CopyHelper.copy(testBubbleA, SnapshotVersion.CURRENT);
        assertThat(copyBubble).usingRecursiveComparison().isEqualTo(testBubbleA);
    }


}
