package no.statkart.skif.storetest.mockup;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFacade;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.storetest.domain.relation.X1AAMockupFactory;
import no.statkart.skif.storetest.domain.relation.X1BBOneMockupFactory;
import no.statkart.skif.storetest.domain.relation.X1CCManyMockupFactory;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2AAWithEntityComponentMockupFactory;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2BBOneMockupFactory;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2CCManyMockupFactory;

import java.util.List;

/**
 * MockupFacade for StoreTest tester
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@Singleton
public class StoreTestMockupFacade extends AbstractMockupFacade {

    @Inject private SimpleMockupFactory simpleMockupFactory;
    @Inject private BubbleWithRelationMockupFactory bubbleWithRelationMockupFactory;
    @Inject private BubbleWithAnyBubbleRefMockupFactory bubbleWithAnyBubbleRefMockupFactory;
    @Inject private BubbleWithFilterMockupFactory bubbleWithFilterMockupFactory;
    @Inject private BubbleWithKodeMockupFactory bubbleWithKodeMockupFactory;
    @Inject private BubbleWithValueObjectMockupFactory bubbleWithValueObjectMockupFactory;

    @Inject private HistSimpleMockupFactory histSimpleMockupFactory;
    @Inject private HistWithRelationMockupFactory histWithRelationMockupFactory;
    @Inject private BubbleWithCompositeComponentMockupFactory bubbleWithCompositeComponentMockupFactory;
    @Inject private BubbleWithEntityComponentMockupFactory bubbleWithEntityComponentMockupFactory;
    @Inject private BubbleWithEntityInCompositeComponentMockupFactory bubbleWithEntityInCompositeComponentMockupFactory;
    @Inject private BubbleWithSubtypedEntityComponentMockupFactory bubbleWithSubtypedEntityComponentMockupFactory;
    @Inject private BubbleWithSubtypedEntityComponentSetMockupFactory bubbleWithSubtypedEntityComponentSetMockupFactory;


    @Inject private X1BBOneMockupFactory x1BBOneMockupFactory;
    @Inject private X1CCManyMockupFactory x1CCManyMockupFactory;
    @Inject private X1AAMockupFactory x1AAMockupFactory;

    @Inject private X2BBOneMockupFactory x2BBOneMockupFactory;
    @Inject private X2CCManyMockupFactory x2CCManyMockupFactory;
    @Inject private X2AAWithEntityComponentMockupFactory x2AAWithEntityComponentMockupFactory;

    @Inject private SubTypedBubbleMockupFactory subTypedBubbleMockupFactory;

    @Override
    public List<? extends AbstractMockupFactory> getAllMockupFactories() {
        return ImmutableList.of(
                simpleMockupFactory,
                bubbleWithRelationMockupFactory,
                bubbleWithAnyBubbleRefMockupFactory,
                bubbleWithFilterMockupFactory,
                bubbleWithValueObjectMockupFactory,
                bubbleWithKodeMockupFactory,
                histSimpleMockupFactory,
                histWithRelationMockupFactory,
                bubbleWithCompositeComponentMockupFactory,
                bubbleWithEntityComponentMockupFactory,
                bubbleWithEntityInCompositeComponentMockupFactory,
                bubbleWithSubtypedEntityComponentMockupFactory,
                bubbleWithSubtypedEntityComponentSetMockupFactory,
                x1BBOneMockupFactory,
                x1CCManyMockupFactory,
                x1AAMockupFactory,
                x2BBOneMockupFactory,
                x2CCManyMockupFactory,
                x2AAWithEntityComponentMockupFactory,
                subTypedBubbleMockupFactory
        );
    }

    public SimpleMockupFactory getSimpleMockupFactory() {
        return simpleMockupFactory;
    }

    public BubbleWithAnyBubbleRefMockupFactory getBubbleWithAnyBubbleRefMockupFactory() {
        return bubbleWithAnyBubbleRefMockupFactory;
    }

    public BubbleWithRelationMockupFactory getBubbleWithRelationMockupFactory() {
        return bubbleWithRelationMockupFactory;
    }
    public BubbleWithFilterMockupFactory getBubbleWithFilterMockupFactory() {
        return bubbleWithFilterMockupFactory;
    }

    public BubbleWithValueObjectMockupFactory getBubbleWithValueObjectMockupFactory() {
        return bubbleWithValueObjectMockupFactory;
    }

    public BubbleWithKodeMockupFactory getBubbleWithKodeMockupFactory() {
        return bubbleWithKodeMockupFactory;
    }

    public HistSimpleMockupFactory getHistSimpleMockupFactory() {
        return histSimpleMockupFactory;
    }

    public HistWithRelationMockupFactory getHistWithRelationMockupFactory() {
        return histWithRelationMockupFactory;
    }

    public SubTypedBubbleMockupFactory getSubTypedBubbleMockupFactory() {
        return subTypedBubbleMockupFactory;
    }
    
    public BubbleWithCompositeComponentMockupFactory getBubbleWithCompositeComponentMockupFactory() {
        return bubbleWithCompositeComponentMockupFactory;
    }

    public BubbleWithEntityComponentMockupFactory getBubbleWithEntityComponentMockupFactory() {
        return bubbleWithEntityComponentMockupFactory;
    }

    public BubbleWithEntityInCompositeComponentMockupFactory getBubbleWithEntityInCompositeComponentMockupFactory() {
        return bubbleWithEntityInCompositeComponentMockupFactory;
    }

    public BubbleWithSubtypedEntityComponentMockupFactory getBubbleWithSubtypedEntityComponentMockupFactory() {
        return bubbleWithSubtypedEntityComponentMockupFactory;
    }

    public BubbleWithSubtypedEntityComponentSetMockupFactory getBubbleWithSubtypedEntityComponentSetMockupFactory() {
        return bubbleWithSubtypedEntityComponentSetMockupFactory;
    }

    public X1AAMockupFactory getX1AAMockupFactory() {
        return x1AAMockupFactory;
    }

    public X1BBOneMockupFactory getX1BBOneMockupFactory() {
        return x1BBOneMockupFactory;
    }

    public X1CCManyMockupFactory getX1CCManyMockupFactory() {
        return x1CCManyMockupFactory;
    }

    public X2BBOneMockupFactory getX2BBOneMockupFactory() {
        return x2BBOneMockupFactory;
    }

    public X2CCManyMockupFactory getX2CCManyMockupFactory() {
        return x2CCManyMockupFactory;
    }

    public X2AAWithEntityComponentMockupFactory getX2AAWithEntityComponentMockupFactory() {
        return x2AAWithEntityComponentMockupFactory;
    }
}

