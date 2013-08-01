package no.statkart.skif.storetest.domain.relation;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistory;

/**
 * Abstract bubleobjet objekt som har nr og text felter
 * @author Henrik Fredholm
 * @since 2.3
 */
public abstract class AbstractRelationTestBubble extends AbstractStoreTestBubbleWithHistory {
    private static final long serialVersionUID = 1L;

    private int nr;
    private String text;

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
}
