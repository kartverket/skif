package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.mockup.Bar;
import no.statkart.skif.storetest.domain.mockup.BarId;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.mockup.FooId;

import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BarFoos extends AbstractBubbleObject implements StoreTestBubble {

    private String text;
    private BarId<Bar> barId;
    private Set<FooId<Foo>> fooIds;

    @Override
    public BarFoosId<?> getId() {
        return (BarFoosId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BarId<Bar> getBarId() {
        return barId;
    }

    public Bar getBar() {
        return store.get(barId);
    }


    public void setBarId(BarId<Bar> barId) {
        this.barId = barId;
    }

    public Set<FooId<Foo>> getFooIds() {
        return fooIds;
    }

    public Set<Foo> getFoos() {
        return store().get(fooIds);
    }

    public void setFooIds(Set<FooId<Foo>> fooIds) {
        this.fooIds = fooIds;
    }
}
