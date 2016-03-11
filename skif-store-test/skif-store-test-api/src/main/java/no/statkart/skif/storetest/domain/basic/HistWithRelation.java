package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistory;

/**
 * Boble med historikk med relasjon til HistSimple
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class HistWithRelation extends AbstractStoreTestBubbleWithHistory {
    private static final long serialVersionUID = 1L;

    private int nr;
    private String text;
    private int testSetNumber;
    private HistSimpleId<?> histSimpleId;

    public HistWithRelation() {
    }

    public HistWithRelation(HistWithRelationId<?> id) {
        super(id);
    }

    @Override
    public HistWithRelationId<?> getId() {
        return (HistWithRelationId<?>) super.getId();
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTestSetNumber() {
        return testSetNumber;
    }

    public void setTestSetNumber(int testSetNumber) {
        this.testSetNumber = testSetNumber;
    }

    public HistSimpleId<?> getHistSimpleId() {
        return histSimpleId;
    }

    public void setHistSimpleId(HistSimpleId<?> histSimpleId) {
        this.histSimpleId = histSimpleId;
    }
}
