package no.statkart.skif.storetest.id;

import no.statkart.skif.storetest.service.id.SequenceBlockAllocatorService;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test som kjøre på server
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
@Test
public class SequenceBlockAllocatorServiceServerTest extends StoreTestServerTestCase {

    public void  testAllocateSequenceBlock(){
        SequenceBlockAllocatorService service = injector.getInstance(SequenceBlockAllocatorService.class);
        long seqNo = service.allocateSequenceBlock("GLOBAL_SEQUENCE", 10);
        Assert.assertTrue(seqNo > 0);

        long seqNo2 = service.allocateSequenceBlock("GLOBAL_SEQUENCE", 10);
        Assert.assertEquals(seqNo2, seqNo + 10);

        long seqNo3 = service.allocateSequenceBlock("GLOBAL_SEQUENCE", 5);
        Assert.assertEquals(seqNo3, seqNo2 + 5);
    }
}
