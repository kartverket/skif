package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFacadeFactory;
import no.statkart.skif.storetest.service.test.TestdataService;

/**
 * Implementasjon av mockupfacadebuilder for storetest-applikasjonen.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Singleton
public class MockupFacadeFactory extends AbstractMockupFacadeFactory<MockupFacade> {
    @Inject
    public MockupFacadeFactory(TestdataService testdataService) {
        super(MockupFacade.class, testdataService);
    }
}
