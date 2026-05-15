package no.statkart.skif.standalone.store;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.standalone.TestBubble;
import no.statkart.skif.storetest.domain.standalone.TestBubbleId;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test
public class BubbleIdTest  {

    public void testTestBubbleId() {
        assertEquals(BubbleIds.getValueType(TestBubbleId.class), Long.class);
        TestBubbleId<?> testBubbleId = BubbleIds.createInstance(TestBubbleId.class, 10L, SnapshotVersion.CURRENT);
        assertEquals(testBubbleId.getValue(), Long.valueOf(10L));
        assertEquals(testBubbleId.getValueType(), Long.class);
        assertEquals(testBubbleId.getSnapshotVersion(), SnapshotVersion.CURRENT);

        TestBubble testBubble = testBubbleId.createTypeInstance();
        assertNotNull(testBubble);
    }

    /**
     * Compatibility with Mockito 3.4
     * https://github.com/mockito/mockito/issues/1898
     */
    public void bubbleIdForMockitoAndHashCode() {
        TestBubbleId<?> testBubbleId = mock(TestBubbleId.class, RETURNS_DEEP_STUBS);

        assertThatCode(testBubbleId::toString)
            .doesNotThrowAnyException();

        Mockito.when(testBubbleId.getValue()).thenReturn(11L);

        assertThat(String.valueOf(testBubbleId))
            .describedAs("Non null toString fixed in Mockito 3.4.0")
            .isNotNull();
    }

    /**
     * Compatibility with Mockito 3.4
     * https://github.com/mockito/mockito/issues/1898
     */
    public void bubbleObjectForMockitoAndHashCode() {
        TestBubble testBubble = mock(TestBubble.class, RETURNS_DEEP_STUBS);

        assertThatCode(testBubble::toString)
            .doesNotThrowAnyException();

        Mockito.when(testBubble.getId()).thenReturn(new TestBubbleId<>(11L));

        assertThat(String.valueOf(testBubble))
            .describedAs("Non null toString fixed in Mockito 3.4.0")
            .isNotNull();
    }
}

