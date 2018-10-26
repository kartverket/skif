package no.statkart.skif.storetest.domain.standalone;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObjectWithHistory;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.sql.Timestamp;

/**
 * Boble som har historikk.
 * <p/>
 * Denne boblen må kun brukes av lavnivå tester som går direkte mot databasen uten å bruke StoreTestServer modulen og
 * skal ikke bruke mockuprammeverket. Objekter med id <= 100 er readonly og skal ikke endres. Objekter med id >
 * 100 slettes automatisk mellom hver testmetode.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class TestBubbleWithHistory extends AbstractBubbleObject implements StoreTestBubble, BubbleObjectWithHistory {
    private Timestamp oppdateringsdato;
    private Timestamp sluttdato;
    private long versjonId;
    private String text = "";

    public TestBubbleWithHistory() {
    }

    public TestBubbleWithHistory(TestBubbleId<?> id) {
        this(id, null);
    }

    public TestBubbleWithHistory(TestBubbleId<?> id, String text) {
        super(id);
        this.text = text;
    }

    @Override
    public void setId(BubbleId<?> id) {
        super.setId(id);
    }

    @Override
    public TestBubbleWithHistoryId<?> getId() {
        return (TestBubbleWithHistoryId<?>) super.getId();
    }

    public Timestamp getOppdateringsdato() {
        return oppdateringsdato;
    }

    public void setOppdateringsdato(Timestamp oppdateringsdato) {
        this.oppdateringsdato = oppdateringsdato;
    }

    public Timestamp getSluttdato() {
        return sluttdato;
    }

    public void setSluttdato(Timestamp sluttdato) {
        this.sluttdato = sluttdato;
    }

    public long getVersjonId() {
        return versjonId;
    }

    public void setVersjonId(long versjonId) {
        this.versjonId = versjonId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}