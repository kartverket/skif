package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class Foo extends AbstractBubbleObject implements StoreTestBubble {

    private long nr;
    private String navn;

    @Override
    public FooId<?> getId() {
        return (FooId<?>) super.getId();
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public long getNr() {
        return nr;
    }

    public void setNr(long nr) {
        this.nr = nr;
    }
}
