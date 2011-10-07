package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class Bar extends AbstractBubbleObject implements StoreTestBubble {

    private long husnr;
    private String bokstav;
    private FooId<Foo> fooId;
    private BazId<Baz> bazId;

    @Override
    public BarId<?> getId() {
        return (BarId<?>) super.getId();
    }

    public String getBokstav() {
        return bokstav;
    }

    public void setBokstav(String bokstav) {
        this.bokstav = bokstav;
    }

    public FooId<Foo> getFooId() {
        return fooId;
    }

    public void setFooId(FooId<Foo> fooId) {
        this.fooId = fooId;
    }

    public BazId<Baz> getBazId() {
        return bazId;
    }

    public void setBazId(BazId<Baz> bazId) {
        this.bazId = bazId;
    }

    public long getHusnr() {
        return husnr;
    }

    public void setHusnr(long husnr) {
        this.husnr = husnr;
    }
}
