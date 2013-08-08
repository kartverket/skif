package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistory;

/**
 * Boble med historikk og som ikke har egne relasjoner til andre objekter
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class HistSimple extends AbstractStoreTestBubbleWithHistory{
    private static final long serialVersionUID = 1L;

    private long nr;
    private String text;
    private int testSetNumber;

    public HistSimple() {
    }

    public HistSimple(HistSimpleId<?> id) {
        super(id);
    }

    @Override
    public HistSimpleId<?> getId() {
        return (HistSimpleId<?>) super.getId();
    }

    public long getNr() {
        return nr;
    }

    public void setNr(long nr) {
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
}
