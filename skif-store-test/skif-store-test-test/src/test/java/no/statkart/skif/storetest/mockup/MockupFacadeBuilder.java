package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Injector;
import no.statkart.skif.mockup.AbstractMockupFacadeBuilder;
import no.statkart.skif.service.test.TestNumberService;

/**
 * Implementasjon av mockupfacadebuilder for storetest-applikasjonen.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class MockupFacadeBuilder extends AbstractMockupFacadeBuilder<MockupFacade> {
    @Inject
    public MockupFacadeBuilder(Injector injector, TestNumberService testNumberService) {
        super(MockupFacade.class, injector, testNumberService);
    }
}
