package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.Test;

/**
 * Tester at mockup virker på tjenersiden også.
 *
 * @author Tor Egil R. Strand
 * @since 2.1.1
 */
@Test
public class MockupServerTest extends StoreTestMixedTestCase {
    public void testWriteSet() {
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private MockupFacadeFactory mockupFacadeFactory;

            @Inject
            private TestdataService testdataService;

            @Override
            public Object run() {
                MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();

                testdataService.saveAll(mockupFacade.getAllTransfers());

                return null;
            }
        });
    }
}
