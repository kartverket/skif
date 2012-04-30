package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Inject;
import no.statkart.skif.mockup.AbstractMockupFacade;

/**
 * @author rorchr
 */
public class TinglysingMockupFacade extends AbstractMockupFacade {

    @Inject
    private PersonMockupFactory personMockupFactory;

    @Inject
    private KommuneMockupFactory kommuneMockupFactory;

    @Inject
    private MatrikkelenhetMockupFactory matrikkelenhetMockupFactory;

    public PersonMockupFactory getPersonMockupFactory() {
        return personMockupFactory;
    }

    public KommuneMockupFactory getKommuneMockupFactory() {
        return kommuneMockupFactory;
    }

    public MatrikkelenhetMockupFactory getMatrikkelenhetMockupFactory() {
        return matrikkelenhetMockupFactory;
    }

    @Override
    public void createAllMockups() {
        personMockupFactory.createAllMockups();
        kommuneMockupFactory.createAllMockups();
        matrikkelenhetMockupFactory.createAllMockups();
    }
}
