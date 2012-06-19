package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFacadeBuilder;
import no.statkart.skif.storetest.service.test.TestdataService;

/**
 * Implementasjon av mockupfacadebuilder for storetest-applikasjonen.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Singleton
public class MockupFacadeBuilder extends AbstractMockupFacadeBuilder<MockupFacade> {
    @Inject
    public MockupFacadeBuilder(TestdataService testdataService) {
        super(MockupFacade.class, testdataService);
    }
}
