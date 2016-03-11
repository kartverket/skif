package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.basic.BubbleWithAnyBubbleRef;
import no.statkart.skif.storetest.domain.basic.BubbleWithAnyBubbleRefId;
import no.statkart.skif.storetest.domain.basic.SomeIdent;

/**
 * @author Henrik Fredholm
 * @since 2.6
 */
@Singleton
public class BubbleWithAnyBubbleRefMockupFactory extends AbstractMockupFactory {
    private final BubbleWithAnyBubbleRefId<?> bubbleWithAnyBubbleRefId1;
    private final BubbleWithAnyBubbleRefId<?> bubbleWithAnyBubbleRefId2;
    private final BubbleWithAnyBubbleRefId<?> bubbleWithAnyBubbleRefId3;

    @Inject SimpleMockupFactory simpleMockupFactory;
    @Inject BubbleWithRelationMockupFactory bubbleWithRelationMockupFactory;


    @Inject
    public BubbleWithAnyBubbleRefMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);
        bubbleWithAnyBubbleRefId1 = getNextId();
        bubbleWithAnyBubbleRefId2 = getNextId();
        bubbleWithAnyBubbleRefId3 = getNextId();
    }

    private BubbleWithAnyBubbleRefId<?> getNextId() {
        return getNextId(BubbleWithAnyBubbleRefId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createBubbleWithAnyBubbleRef(bubbleWithAnyBubbleRefId1, getTestNumber().getNumber(), simpleMockupFactory.getSimpleId2()));
        store.insert(createBubbleWithAnyBubbleRef(bubbleWithAnyBubbleRefId2, getTestNumber().getNumber(), null));
        store.insert(createBubbleWithAnyBubbleRef(bubbleWithAnyBubbleRefId3, getTestNumber().getNumber(), bubbleWithRelationMockupFactory.getBubbleWithRelationId1()));
    }

    private BubbleWithAnyBubbleRef createBubbleWithAnyBubbleRef(BubbleWithAnyBubbleRefId<?> aId, int nr, BubbleId<?> anyId ) {
        BubbleWithAnyBubbleRef a = new BubbleWithAnyBubbleRef();
        a.setId(aId);
        a.setNr(nr);
        a.setAnyId(anyId);
        a.setSomeIdent(new SomeIdent(nr, anyId==null ? null : anyId.getValue().toString()));
       return a;
    }

    public BubbleWithAnyBubbleRefId<?> getBubbleWithAnyBubbleRefId1() {
        return bubbleWithAnyBubbleRefId1;
    }

    public BubbleWithAnyBubbleRefId<?> getBubbleWithAnyBubbleRefId2() {
        return bubbleWithAnyBubbleRefId2;
    }

    public BubbleWithAnyBubbleRefId<?> getBubbleWithAnyBubbleRefId3() {
        return bubbleWithAnyBubbleRefId3;
    }
}
