package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Inject;
import no.statkart.skif.mockup.AbstractMockupFacade;

/**
 * @author rorchr
 */
public class TinglysingMockupFacade extends AbstractMockupFacade {

    @Inject
    private AndelIMatrikkelenhetMockupFactory andelIMatrikkelenhetMockupFactory;

    @Inject
    private DokumentMockupFactory dokumentMockupFactory;

    @Inject
    private HjemmelForMatrikkelenhetMockupFactory hjemmelForMatrikkelenhetMockupFactory;

    @Inject
    private HjemmelForPersonMockupFactory hjemmelForPersonMockupFactory;

    @Inject
    private KommuneMockupFactory kommuneMockupFactory;

    @Inject
    private MatrikkelenhetMockupFactory matrikkelenhetMockupFactory;

    @Inject
    private NivaaIMatrikkelenhetMockupFactory nivaaIMatrikkelenhetMockupFactory;

    @Inject
    private PersonMockupFactory personMockupFactory;

    public AndelIMatrikkelenhetMockupFactory getAndelIMatrikkelenhetMockupFactory() {
        return andelIMatrikkelenhetMockupFactory;
    }

    public KommuneMockupFactory getKommuneMockupFactory() {
        return kommuneMockupFactory;
    }

    public MatrikkelenhetMockupFactory getMatrikkelenhetMockupFactory() {
        return matrikkelenhetMockupFactory;
    }

    public NivaaIMatrikkelenhetMockupFactory getNivaaIMatrikkelenhetMockupFactory() {
        return nivaaIMatrikkelenhetMockupFactory;
    }

    public PersonMockupFactory getPersonMockupFactory() {
        return personMockupFactory;
    }

    public DokumentMockupFactory getDokumentMockupFactory() {
        return dokumentMockupFactory;
    }

    public HjemmelForPersonMockupFactory getHjemmelForPersonMockupFactory() {
        return hjemmelForPersonMockupFactory;
    }

    public HjemmelForMatrikkelenhetMockupFactory getHjemmelForMatrikkelenhetMockupFactory() {
        return hjemmelForMatrikkelenhetMockupFactory;
    }

    @Override
    public void createAllMockups() {
        personMockupFactory.createAllMockups();
        kommuneMockupFactory.createAllMockups();
        matrikkelenhetMockupFactory.createAllMockups();
        nivaaIMatrikkelenhetMockupFactory.createAllMockups();
        andelIMatrikkelenhetMockupFactory.createAllMockups();
        dokumentMockupFactory.createAllMockups();
        hjemmelForMatrikkelenhetMockupFactory.createAllMockups();
        hjemmelForPersonMockupFactory.createAllMockups();
    }
}
