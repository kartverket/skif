package no.statkart.skif.mockup;

import no.statkart.skif.store.BubbleId;

import java.util.Set;

/**
 * Funksjonelt interface for å angi spesifikke id-er ved oppretting av mockup facade.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public interface IdSelector<T extends AbstractMockupFacade> {

    /**
     * Angir hvilke id-er fra testsettet som skal lagres ned.
     *
     * @param mockupFacade mockupfacade for testsettet
     * @return id-ene som skal lagres
     */
    Set<? extends BubbleId> selectFrom(T mockupFacade);

}
