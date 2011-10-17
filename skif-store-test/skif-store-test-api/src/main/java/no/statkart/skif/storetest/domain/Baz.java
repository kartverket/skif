package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.AbstractBubbleObject;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class Baz extends AbstractBubbleObject implements StoreTestBubble {

    private String text;
    FooId<Foo> fooId;

    @Override
    public BazId<?> getId() {
        return (BazId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FooId<Foo> getFooId() {
        return fooId;
    }

    public void setFooId(FooId<Foo> fooId) {
        this.fooId = fooId;
    }
}
