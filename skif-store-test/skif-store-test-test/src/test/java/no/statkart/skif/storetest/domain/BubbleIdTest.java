package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 */
@Test
public class BubbleIdTest  {

    public void testTestBubbleId() {
        assertEquals(BubbleIds.getValueType(TestBubbleId.class), Long.class);
        TestBubbleId<?> testBubbleId = BubbleIds.createInstance(TestBubbleId.class, new Long(10), SnapshotVersion.CURRENT);
        assertEquals(testBubbleId.getValue(), new Long(10));
        assertEquals(testBubbleId.getValueType(), Long.class);
        assertEquals(testBubbleId.getSnapshotVersion(), SnapshotVersion.CURRENT);

        TestBubble testBubble = testBubbleId.createTypeInstance();
        assertNotNull(testBubble);
    }
}

