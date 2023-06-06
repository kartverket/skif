package no.statkart.skif.standalone.store;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.standalone.TestBubble;
import no.statkart.skif.storetest.domain.standalone.TestBubbleId;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Henrik Fredholm
 */
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
}

