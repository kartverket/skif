package no.statkart.skif.store;

import java.sql.Timestamp;

/**
 * Mulig baseklasse for bobler med historikk.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class AbstractBubbleObjectWithHistory extends AbstractBubbleObject implements BubbleObjectWithHistory {
    private static final long serialVersionUID = 1L;

    private Timestamp oppdateringsdato;
    private Timestamp sluttdato;

    @Override
    public Timestamp getOppdateringsdato() {
        return oppdateringsdato;
    }

    @Override
    public void setOppdateringsdato(Timestamp oppdateringsdato) {
        this.oppdateringsdato = oppdateringsdato;
    }

    @Override
    public Timestamp getSluttdato() {
        return sluttdato;
    }

    @Override
    public void setSluttdato(Timestamp sluttdato) {
        this.sluttdato = sluttdato;
    }
}
