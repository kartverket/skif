package no.statkart.skif.storetest.mockup;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFacade;
import no.statkart.skif.mockup.AbstractMockupFactory;
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
    @Inject private HistSimpleMockupFactory histSimpleMockupFactory;
    @Inject private HistWithRelationMockupFactory histWithRelationMockupFactory;

    @Inject private X1AMockupFactory x1AMockupFactory;
    @Inject private X1BOneMockupFactory x1BOneMockupFactory;

    @Override
    public List<? extends AbstractMockupFactory> getAllMockupFactories() {
        return ImmutableList.of(
                simpleMockupFactory,
                bubbleWithRelationMockupFactory,
                bubbleWithFilterMockupFactory,
                histSimpleMockupFactory,
                histWithRelationMockupFactory,
                x1AMockupFactory,
                x1BOneMockupFactory
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

    public HistSimpleMockupFactory getHistSimpleMockupFactory() {
        return histSimpleMockupFactory;
    }

    public X1AMockupFactory getX1AMockupFactory() {
        return x1AMockupFactory;
    }

    public X1BOneMockupFactory getX1BOneMockupFactory() {
        return x1BOneMockupFactory;
    }
}
