package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import no.statkart.skif.mockup.AbstractMockupFacade;
import no.statkart.skif.mockup.AbstractMockupFactory;
import org.testng.collections.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * Mockupfacade for storetest-applikasjonen.
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
