package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

/**
 * Boble uten historikk med relasjon til boble Simple
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithRelation extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private int nr;
    private String text;
    private SimpleId<?> simpleId;

    public BubbleWithRelation() {
    }

    public BubbleWithRelation(BubbleWithRelationId<?> id) {
        super(id);
    }

    @Override
    public BubbleWithRelationId<?> getId() {
        return (BubbleWithRelationId<?>) super.getId();
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

    public SimpleId<?> getSimpleId() {
        return simpleId;
    }

    public void setSimpleId(SimpleId<?> simpleId) {
        this.simpleId = simpleId;
    }
}
