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

        final Long value = 11L;
        final String hashCodeAsString = String.valueOf(value.hashCode());
        Mockito.when(testBubbleId.getValue()).thenReturn(value);

        assertThat(String.valueOf(testBubbleId))
                .describedAs("Non null toString fixed in Mockito 3.4.0")
                .isNotNull()
                .describedAs("Default toString for mock contains calculated hashCode() value")
                .contains(hashCodeAsString);
    }

    /**
     * Compatibility with Mockito 3.4
     * https://github.com/mockito/mockito/issues/1898
     */
    public void bubbleObjectForMockitoAndHashCode() {
        TestBubble testBubble = mock(TestBubble.class, RETURNS_DEEP_STUBS);

        assertThatCode(testBubble::toString)
                .doesNotThrowAnyException();

        final TestBubbleId value = new TestBubbleId<>(11L);
        final String hashCodeAsString = String.valueOf(value.hashCode());
        Mockito.when(testBubble.getId()).thenReturn(value);

        assertThat(String.valueOf(testBubble))
                .describedAs("Non null toString fixed in Mockito 3.4.0")
                .isNotNull()
                .describedAs("Default toString for mock contains calculated hashCode() value")
                .contains(hashCodeAsString);
    }

}

