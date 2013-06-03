package no.statkart.skif.storetest2.domain;

import java.sql.Timestamp;

/**
 * Baseklasse for bobler med historikk.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
public class AbstractStoreTest2BubbleWithHistory extends AbstractStoreTest2Bubble {
    private static final long serialVersionUID = 1L;

    private Timestamp oppdateringsdato;
    private Timestamp sluttdato;
    private long versjonId;

    public Timestamp getOppdateringsdato() {
        return oppdateringsdato;
    }

    // For Hibernate
    private void setOppdateringsdato(Timestamp oppdateringsdato) {
        this.oppdateringsdato = oppdateringsdato;
    }

    public Timestamp getSluttdato() {
        return sluttdato;
    }

    // For Hibernate
    private void setSluttdato(Timestamp sluttdato) {
        this.sluttdato = sluttdato;
    }

    public long getVersjonId() {
        return versjonId;
    }

    // For Hibernate
    private void setVersjonId(long versjonId) {
        this.versjonId = versjonId;
    }

    @Override
    public AbstractStoreTest2BubbleWithHistoryId<?> getId() {
        return (AbstractStoreTest2BubbleWithHistoryId<?>) super.getId();
    }
}
