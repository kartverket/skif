package no.statkart.skif.storetest.mockupframework;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.Test;

/**
 * Tester at mockup ramemverktet virker på tjenersiden også.
 * <p>
 * Denne testen bruker en helt egen lille MockupFacadeFactory som inneholder begrenset antall klasser og
 * som ikke brukes for annen testing.
 *
 * @author Tor Egil R. Strand
 * @since 2.1.1
 */
@Test(groups="singlevm-required")
public class MockupFrameworkServerTest extends StoreTestMixedTestCase {
    public void testWriteSet() {
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private MockupFacadeFactory mockupFacadeFactory;

            @Inject
            private TestdataService testdataService;

            @Override
            public Object run() {
                MockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();

                testdataService.saveAll(mockupFacade.getAllTransfers());

                return null;
            }
        });
    }
}
