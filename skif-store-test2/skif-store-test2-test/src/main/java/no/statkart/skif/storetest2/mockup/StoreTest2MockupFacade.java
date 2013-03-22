package no.statkart.skif.storetest2.mockup;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import no.statkart.skif.mockup.AbstractMockupFacade;
import no.statkart.skif.mockup.AbstractMockupFactory;

import java.util.List;

/**
 * Mockup facade for StoreTest2-prosjektet.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class StoreTest2MockupFacade extends AbstractMockupFacade {
    @Inject private EiendomMockupFactory eiendomMockupFactory;
    @Inject private EierMockupFactory eierMockupFactory;

    @Override
    public List<? extends AbstractMockupFactory> getAllMockupFactories() {
        return ImmutableList.of(
                eiendomMockupFactory,
                eierMockupFactory
        );
    }

    public EiendomMockupFactory getEiendomMockupFactory() {
        return eiendomMockupFactory;
    }

    public EierMockupFactory getEierMockupFactory() {
        return eierMockupFactory;
    }
}
