package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleObjectWithHistory;

import java.sql.Timestamp;

/**
 * Baseklasse for bobler med historikk.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public abstract class AbstractStoreTestBubbleWithHistory extends AbstractStoreTestBubble implements BubbleObjectWithHistory {
    private static final long serialVersionUID = 1L;

    private Timestamp oppdateringsdato;
    private Timestamp sluttdato;
    private long versjonId;

    protected AbstractStoreTestBubbleWithHistory() {
    }

    protected AbstractStoreTestBubbleWithHistory(AbstractStoreTestBubbleWithHistoryId<?> id) {
        super(id);
    }


    public boolean sameVersion(AbstractStoreTestBubbleWithHistory o) {
        if (!this.getId().getValue().equals(o.getId().getValue())) return false;
        if (!this.getOppdateringsdato().equals(o.getOppdateringsdato())) return false;
        if (!this.getSluttdato().equals(o.getSluttdato())) return false;
        return true;
    }

    public Timestamp getOppdateringsdato() {
        return oppdateringsdato;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    @Deprecated // WS-mapping krever public
    public void setOppdateringsdato(Timestamp oppdateringsdato) {
        this.oppdateringsdato = oppdateringsdato;
    }

    public Timestamp getSluttdato() {
        return sluttdato;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    @Deprecated // WS-mapping krever public
    public void setSluttdato(Timestamp sluttdato) {
        this.sluttdato = sluttdato;
    }

    public long getVersjonId() {
        return versjonId;
    }

    public void setVersjonId(long versjonId) {
        this.versjonId = versjonId;
    }

    @Override
    public AbstractStoreTestBubbleWithHistoryId<?> getId() {
        return (AbstractStoreTestBubbleWithHistoryId<?>) super.getId();
    }
}
