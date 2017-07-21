package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C2DbKodeId;

/**
 * Boble uten historikk og som ikke har egne relasjoner til andre objekter
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithKode extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private int nr;
    private String text;
    private AEnumKodeId testAEnumKodeId = AEnumKodeId.IkkeOppgittId;
    private C2DbKodeId testC2DbKodeId = C2DbKodeId.C2A1Id;

    public BubbleWithKode() {
    }

    @Override
    public BubbleWithKodeId<?> getId() {
        return (BubbleWithKodeId<?>) super.getId();
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
