package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.BubbleWithKode;
import no.statkart.skif.storetest.domain.basic.BubbleWithKodeId;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C2DbKodeId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class BubbleWithKodeMockupFactory extends AbstractMockupFactory {
    private final BubbleWithKodeId<?> bubbleWithKodeId1;

    @Inject
    public BubbleWithKodeMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        bubbleWithKodeId1 = getNextId();
    }

    private BubbleWithKodeId<?> getNextId() {
        return getNextId(BubbleWithKodeId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createBubbleWithKode(bubbleWithKodeId1, 1, null, AEnumKodeId.KodeAId, C2DbKodeId.C2BId));
    }

    private BubbleWithKode createBubbleWithKode(BubbleWithKodeId<?> id, int nr, String text, AEnumKodeId aEnumKodeId,C2DbKodeId c2DbKodeId) {
        BubbleWithKode obj = new BubbleWithKode();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        obj.setTestAEnumKodeId(aEnumKodeId);
        obj.setTestC2DbKodeId(c2DbKodeId);
       return obj;
    }

    public BubbleWithKodeId<?> getBubbleWithKodeId1() {
        return bubbleWithKodeId1;
    }
}
