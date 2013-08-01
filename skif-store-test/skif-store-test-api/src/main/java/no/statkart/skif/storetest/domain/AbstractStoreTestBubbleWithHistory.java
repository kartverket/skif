package no.statkart.skif.storetest.domain;

import java.sql.Timestamp;

/**
 * Baseklasse for bobler med historikk.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public abstract class AbstractStoreTestBubbleWithHistory extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private Timestamp oppdateringsdato;
    private Timestamp sluttdato;
    private long versjonId;

    protected AbstractStoreTestBubbleWithHistory() {
    }

    protected AbstractStoreTestBubbleWithHistory(AbstractStoreTestBubbleWithHistoryId<?> id) {
        super(id);
    }

    public Timestamp getOppdateringsdato() {
        return oppdateringsdato;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setOppdateringsdato(Timestamp oppdateringsdato) {
        this.oppdateringsdato = oppdateringsdato;
    }

    public Timestamp getSluttdato() {
        return sluttdato;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setSluttdato(Timestamp sluttdato) {
        this.sluttdato = sluttdato;
    }

    public long getVersjonId() {
        return versjonId;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setVersjonId(long versjonId) {
        this.versjonId = versjonId;
    }

    @Override
    public AbstractStoreTestBubbleWithHistoryId<?> getId() {
        return (AbstractStoreTestBubbleWithHistoryId<?>) super.getId();
    }
}
