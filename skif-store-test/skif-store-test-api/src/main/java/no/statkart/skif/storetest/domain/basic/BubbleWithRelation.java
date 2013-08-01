package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.demo.Baz;
import no.statkart.skif.storetest.domain.demo.BazId;
import no.statkart.skif.storetest.domain.mockup.BarId;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.mockup.FooId;

/**
 * Boble uten historikk med relasjon til boble Simple
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithRelation extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private long nr;
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

    public SimpleId<?> getSimpleId() {
        return simpleId;
    }

    public void setSimpleId(SimpleId<?> simpleId) {
        this.simpleId = simpleId;
    }
}
