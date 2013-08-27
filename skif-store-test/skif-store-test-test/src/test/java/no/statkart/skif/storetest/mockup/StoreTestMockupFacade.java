package no.statkart.skif.storetest.mockup;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFacade;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.storetest.domain.basic.BubbleWithValueObject;
import no.statkart.skif.storetest.domain.basic.HistSimple;

import java.util.List;

/**
 * MockupFacade for StoreTest tester
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class StoreTestMockupFacade extends AbstractMockupFacade {

    @Inject private SimpleMockupFactory simpleMockupFactory;
    @Inject private BubbleWithRelationMockupFactory bubbleWithRelationMockupFactory;
    @Inject private BubbleWithFilterMockupFactory bubbleWithFilterMockupFactory;
    @Inject private BubbleWithKodeMockupFactory bubbleWithKodeMockupFactory;
    @Inject private BubbleWithValueObjectMockupFactory bubbleWithValueObjectMockupFactory;

    @Inject private HistSimpleMockupFactory histSimpleMockupFactory;
    @Inject private HistWithRelationMockupFactory histWithRelationMockupFactory;

    @Inject private SubTypedBubbleMockupFactory subTypedBubbleMockupFactory;

    @Override
    public List<? extends AbstractMockupFactory> getAllMockupFactories() {
        return ImmutableList.of(
                simpleMockupFactory,
                bubbleWithRelationMockupFactory,
                bubbleWithFilterMockupFactory,
                bubbleWithValueObjectMockupFactory,
                bubbleWithKodeMockupFactory,
                histSimpleMockupFactory,
                histWithRelationMockupFactory,
                subTypedBubbleMockupFactory
        );
    }

    public SimpleMockupFactory getSimpleMockupFactory() {
        return simpleMockupFactory;
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
}
