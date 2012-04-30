package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Inject;
import com.google.inject.Injector;
import no.statkart.skif.mockup.AbstractMockupFacadeBuilder;
import no.statkart.skif.service.test.TestNumberService;

public class TinglysingMockupFacadeBuilder extends AbstractMockupFacadeBuilder<TinglysingMockupFacade> {
    @Inject
    public TinglysingMockupFacadeBuilder(Injector injector, TestNumberService testNumberService) {
        super(TinglysingMockupFacade.class, injector, testNumberService);
    }
}
