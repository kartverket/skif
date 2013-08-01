package no.statkart.skif.storetest.mockupframework;

import com.google.inject.Inject;
import no.statkart.skif.mockup.AbstractMockupFacade;
import no.statkart.skif.mockup.AbstractMockupFactory;

import java.util.Arrays;
import java.util.List;

/**
 * MockupFacade for isolert testing av mockup rammeverket.
 * <p>
 *
 * NB: denne Mockupfacade kan ikke dele mockup factoies med StoreTestMockupFacade
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class MockupFacade extends AbstractMockupFacade {

    @Inject
    private FooMockupFactory fooMockupFactory;

    public FooMockupFactory getFooMockupFactory() {
        return fooMockupFactory;
    }

    @Override
    public List<? extends AbstractMockupFactory> getAllMockupFactories() {
        return Arrays.asList(fooMockupFactory);
    }
}
