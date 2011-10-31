package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class Baz extends AbstractBubbleObject implements StoreTestBubble {

    private String text;
    FooId<Foo> fooId;
    AEnumKodeId testAEnumKodeId = AEnumKodeId.IkkeOppgittId;

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

    public AEnumKodeId getTestAEnumKodeId() {
        return testAEnumKodeId;
    }

    public void setTestAEnumKodeId(AEnumKodeId testAEnumKodeId) {
        this.testAEnumKodeId = testAEnumKodeId;
    }
}
