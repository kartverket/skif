package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C2DbKodeId;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.mockup.FooId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class Baz extends AbstractBubbleObject implements StoreTestBubble {

    private String text;
    FooId<Foo> fooId;
    AEnumKodeId testAEnumKodeId = AEnumKodeId.IkkeOppgittId;
    C2DbKodeId testC2DbKodeId = C2DbKodeId.C2A1Id;

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

    public C2DbKodeId getTestC2DbKodeId() {
        return testC2DbKodeId;
    }

    public void setTestC2DbKodeId(C2DbKodeId testC2DbKodeId) {
        this.testC2DbKodeId = testC2DbKodeId;
    }
}
