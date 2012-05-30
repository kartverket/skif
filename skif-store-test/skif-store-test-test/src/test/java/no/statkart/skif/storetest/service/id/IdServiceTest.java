package no.statkart.skif.storetest.service.id;

import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class IdServiceTest extends StoreTestTestCase {

    public void testNextValue() {
        IdService idService = injector.getInstance(IdService.class);
        int oldBlockSize = idService.getBlockSize();
        try {
            idService.clear();
            idService.setBlockSize(2);
            final Long idValue1 = Long.class.cast(idService.getNextIdValue(TestBubbleId.class));
            final Long idValue2 = Long.class.cast(idService.getNextIdValue(TestBubbleId.class));
            final Long idValue3 = Long.class.cast(idService.getNextIdValue(TestBubbleId.class));
            assertEquals(new Long(idValue1+1), new Long(idValue2));
            assertTrue(idValue2 < idValue3);
        } finally {
            idService.setBlockSize(oldBlockSize);
        }
    }

    public void testNextId() {
        IdService idService = injector.getInstance(IdService.class);
        int oldBlockSize = idService.getBlockSize();
        try {
            idService.clear();
            idService.setBlockSize(2);
            final TestBubbleId<?> nextId1 = idService.getNextId(TestBubbleId.class);
            final TestBubbleId<?> nextId2 = idService.getNextId(TestBubbleId.class);
            final TestBubbleId<?> nextId3 = idService.getNextId(TestBubbleId.class);
            final Long idValue1 = nextId1.getValue();
            final Long idValue2 = nextId2.getValue();
            final Long idValue3 = nextId3.getValue();
            assertEquals(new Long(idValue1+1), new Long(idValue2));
            assertTrue(idValue2 < idValue3);
        } finally {
            idService.setBlockSize(oldBlockSize);
        }
    }

    public void testIsSingleton() {
        assertSame(injector.getInstance(IdService.class), injector.getInstance(IdService.class));
    }
}
