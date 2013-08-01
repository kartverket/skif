package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistory;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * Boble med historikk med relasjon til HistSimple
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class HistWithRelation extends AbstractStoreTestBubbleWithHistory {
    private static final long serialVersionUID = 1L;

    private long nr;
    private String text;
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

    public HistSimpleId<?> getHistSimpleId() {
        return histSimpleId;
    }

    public void setHistSimpleId(HistSimpleId<?> histSimpleId) {
        this.histSimpleId = histSimpleId;
    }
}
