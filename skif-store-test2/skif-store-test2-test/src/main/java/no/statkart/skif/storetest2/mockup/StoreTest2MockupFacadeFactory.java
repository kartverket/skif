package no.statkart.skif.storetest2.mockup;

import com.google.inject.Inject;
import no.statkart.skif.mockup.AbstractMockupFacadeFactory;
import no.statkart.skif.service.test.TestdataService;

/**
 * Lager {@link StoreTest2MockupFacade}-er.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class StoreTest2MockupFacadeFactory extends AbstractMockupFacadeFactory<StoreTest2MockupFacade> {
    @Inject
    public StoreTest2MockupFacadeFactory(TestdataService testdataService) {
        super(StoreTest2MockupFacade.class, testdataService);
    }
}
