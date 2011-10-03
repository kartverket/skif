package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class Foo extends AbstractBubbleObject implements StoreTestBubble {

    private long a;
    private String b;

    @Override
    public FooId<?> getId() {
        return (FooId<?>) super.getId();
    }

    public long getA() {
        return a;
    }

    public void setA(long a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }
}
