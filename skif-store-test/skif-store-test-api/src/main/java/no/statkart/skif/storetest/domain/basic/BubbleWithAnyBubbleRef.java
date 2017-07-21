package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

/**
 * Boble uten historikk og som kan ha relasjon til en vilkårlig annen bobleId
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithAnyBubbleRef extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private int nr;
    private BubbleId anyId;
    private SomeIdent someIdent;

    public BubbleWithAnyBubbleRef() {
    }

    @Override
    public BubbleWithAnyBubbleRefId<?> getId() {
        return (BubbleWithAnyBubbleRefId<?>) super.getId();
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public BubbleId getAnyId() {
        return anyId;
    }

    public void setAnyId(BubbleId anyId) {
        this.anyId = anyId;
    }

    public SomeIdent getSomeIdent() {
        return someIdent;
    }

    public void setSomeIdent(SomeIdent someIdent) {
        this.someIdent = someIdent;
    }
}
