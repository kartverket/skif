package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelation;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelationId;
import no.statkart.skif.storetest.domain.basic.SimpleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class BubbleWithRelationMockupFactory extends AbstractMockupFactory {
    private final BubbleWithRelationId<?> bubbleWithRelationId1;
    private final BubbleWithRelationId<?> bubbleWithRelationId2;
    private final BubbleWithRelationId<?> bubbleWithRelationId3;

    @Inject SimpleMockupFactory simpleMockupFactory;

    @Inject
    public BubbleWithRelationMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        bubbleWithRelationId1 = getNextId();
        bubbleWithRelationId2 = getNextId();
        bubbleWithRelationId3 = getNextId();
    }

    private BubbleWithRelationId<?> getNextId() {
        return getNextId(BubbleWithRelationId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createBubbleWithRelation(bubbleWithRelationId1, 1, null, simpleMockupFactory.getSimpleId2()));
        store.insert(createBubbleWithRelation(bubbleWithRelationId2, 2, null, simpleMockupFactory.getSimpleId3()));
        store.insert(createBubbleWithRelation(bubbleWithRelationId3, 3, null, simpleMockupFactory.getSimpleId3()));
    }

    private BubbleWithRelation createBubbleWithRelation(BubbleWithRelationId<?> aId, int nr, String text, SimpleId<?> simpleId ) {
        BubbleWithRelation a = new BubbleWithRelation();
        a.setId(aId);
        a.setNr(nr);
        a.setText(text);
        a.setSimpleId(simpleId);
       return a;
    }

    public BubbleWithRelationId<?> getBubbleWithRelationId1() {
        return bubbleWithRelationId1;
    }

    public BubbleWithRelationId<?> getBubbleWithRelationId2() {
        return bubbleWithRelationId2;
    }

    public BubbleWithRelationId<?> getBubbleWithRelationId3() {
        return bubbleWithRelationId3;
    }
}
