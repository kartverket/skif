package no.statkart.skif.skiftest.service.test;

import no.statkart.skif.skiftest.config.SkifTestTxManagementServerModule;
import no.statkart.skif.skiftest.service.id.SequenceBlockAllocatorService;
import no.statkart.skif.skiftest.service.txmanagement.SkifTestTxManagementClientModule;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@Test
public class SequenceBlockAllocatorServiceTest extends SkifTestCase {

    public SequenceBlockAllocatorServiceTest() {
        setModuleClass(SkifTestTxManagementClientModule.class);
        setSingleVmServerModuleClass(SkifTestTxManagementServerModule.class);
    }

    @Test
    public void testAllocateSequenceBlock(){
        SequenceBlockAllocatorService service = injector.getInstance(SequenceBlockAllocatorService.class);
        long seqNo = service.allocateSequenceBlock("GLOBAL_SEQUENCE", 10);
        Assert.assertTrue(seqNo > 0);

        long seqNo2 = service.allocateSequenceBlock("GLOBAL_SEQUENCE", 10);
        Assert.assertEquals(seqNo2, seqNo + 10);

        long seqNo3 = service.allocateSequenceBlock("GLOBAL_SEQUENCE", 5);
        Assert.assertEquals(seqNo3, seqNo2 + 5);
    }


}
