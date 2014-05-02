package no.statkart.skif.storetest.domain.component;


import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityInCompositeComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityInCompositeComponentId;
import no.statkart.skif.storetest.mockup.BubbleWithEntityInCompositeComponentMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.Test;

import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.*;

/**
 * Test av boble med composite component som inneholder entity component
 * @author Henrik Fredholm
 * @since 2.4
 */
@Test(groups = {"singlevm-required","hibernate36"})
public class EntityInCompositeComponentMixedServerTest extends StoreTestMixedTestCase {
    @Inject
    Store store;
    @Inject
    StoreUpdateService storeUpdateService;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;


    private StoreTestMockupFacade getWriteMockupFacadeAndSaveDataForTestSet1() {
        return mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<StoreTestMockupFacade>() {
            @Override
            public Set<? extends BubbleId> selectFrom(StoreTestMockupFacade mockupFacade) {
                return mockupFacade.getBubbleWithEntityInCompositeComponentMockupFactory().getAllIds(BubbleWithEntityInCompositeComponentId.class);
            }
        });
    }

    /**
     * Tester persistering av mockup testset for BubbleWithEntityInCompositeComponent og herunder også "SKIF-428:
     * Hibernate Batch insert ordering does not consider associations i composite components". Når testen kjøres må
     * hibernate sql logging være slått på og man må manulet sjekket at sql inserts batches riktig. Testsettet
     * består av:
     *  - en BubbleWithEntityInCompositeComponent boble som ikke har relasjon til level1 og level2 entities
     *  - to BubbleWithEntityInCompositeComponent bobler med relasjon til level1 entities
     *  - tre BubbleWithEntityInCompositeComponent bobler med relasjon til level1 og level2 entities
     * Dette testsett skal føre til at Hibernate produserer tre insert batchgrupper for
     * BubbleWithEntityInCompositeComponent med henholdsvis en, to, og tre  BubbleWithEntityInCompositeComponent
     * inserts. For updates skal Hibernate kun produsere en update batchgruppe som setter ownerId.
     */
    public void testWriteTestSet1() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
    }

    public void testReadBubbleWithNullCompositeComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithEntityInCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityInCompositeComponentMockupFactory();
        final BubbleWithEntityInCompositeComponent bubbleWithNullComponents = store.get(mockupFactory.getWithNullComponentsId());
        assertEquals(bubbleWithNullComponents.getText(), "Obj " + 1 + " med null components");
        // Composite components som inneholder Set vil aldrig være null da de alltid vil ha et tomt Set i seg.
        assertNotNull(bubbleWithNullComponents.getLevel1Component());
        assertNull(bubbleWithNullComponents.getLevel1Component().getText());
        assertNull(bubbleWithNullComponents.getLevel1Component().getEntity());
        assertThat(bubbleWithNullComponents.getLevel1Component().getEntitySet()).isEmpty();
        assertThat(bubbleWithNullComponents.getLevel1Component().getEntitySet()).isEmpty();
        assertNotNull(bubbleWithNullComponents.getLevel1Component().getLevel2Component());
        assertNull(bubbleWithNullComponents.getLevel1Component().getLevel2Component().getText());
        assertNull(bubbleWithNullComponents.getLevel1Component().getLevel2Component().getEntity());
        assertTrue(bubbleWithNullComponents.getLevel1Component().getLevel2Component().isNullComponent());
        assertThat(bubbleWithNullComponents.getLevel1Component().getLevel2Component().getEntitySet()).isEmpty();
    }

    public void testReadBubbleWithNonNullLevel1AndNullLevel2CompositeComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithEntityInCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityInCompositeComponentMockupFactory();
        final BubbleWithEntityInCompositeComponent bubbleWithNullLevel2Components = store.get(mockupFactory.getWithNullLevel2Id());

        assertEquals(bubbleWithNullLevel2Components.getText(), "Obj 2 med null level2 component");
        assertNotNull(bubbleWithNullLevel2Components.getLevel1Component());
        assertFalse(bubbleWithNullLevel2Components.getLevel1Component().isNullComponent());
        assertNotNull(bubbleWithNullLevel2Components.getLevel1Component().getText());
        assertNotNull(bubbleWithNullLevel2Components.getLevel1Component().getEntity());
        assertThat(bubbleWithNullLevel2Components.getLevel1Component().getEntitySet()).hasSize(1);

        // Composite components som inneholder Set vil aldrig være null da de alltid vil ha en tomt Set.
        assertNotNull(bubbleWithNullLevel2Components.getLevel1Component().getLevel2Component());
        assertTrue(bubbleWithNullLevel2Components.getLevel1Component().getLevel2Component().isNullComponent());
        assertNull(bubbleWithNullLevel2Components.getLevel1Component().getLevel2Component().getText());
        assertNull(bubbleWithNullLevel2Components.getLevel1Component().getLevel2Component().getEntity());
        assertThat(bubbleWithNullLevel2Components.getLevel1Component().getLevel2Component().getEntitySet()).isEmpty();
    }

    public void testReadBubbleWithNonNullLevel1AndLevel2CompositeComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithEntityInCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityInCompositeComponentMockupFactory();
        final BubbleWithEntityInCompositeComponent bubbleWithNonNullComponents = store.get(mockupFactory.getWithNonNullComponentsId());

        assertEquals(bubbleWithNonNullComponents.getText(), "Obj 4 med level1 og level2 component");
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component());
        assertFalse(bubbleWithNonNullComponents.getLevel1Component().isNullComponent());
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component().getText());
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component().getEntity());
        assertThat(bubbleWithNonNullComponents.getLevel1Component().getEntitySet()).hasSize(1);
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component().getLevel2Component());
        assertFalse(bubbleWithNonNullComponents.getLevel1Component().getLevel2Component().isNullComponent());
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component().getLevel2Component().getText());
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component().getLevel2Component().getEntity(), null);
        assertThat(bubbleWithNonNullComponents.getLevel1Component().getLevel2Component().getEntitySet()).hasSize(1);
    }

    public void testUpdateBubbleWithNonNullLevel1AndLevel2CompositeComponentNoChangeInDetatcedState() {
        final StoreTestMockupFacade mockupFacade =getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityInCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityInCompositeComponentMockupFactory();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    BubbleWithEntityInCompositeComponent bubble = store.lock(mockupFactory.getWithNonNullComponentsId());
                    store.update(bubble);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
    }

    public void testDeleteComponentInBubbleWithNonNullLevel1AndLevel2CompositeComponentInDetachedState() {
        final StoreTestMockupFacade mockupFacade =getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityInCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityInCompositeComponentMockupFactory();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                UnitOfWork unitOfWork = store.beginUnitOfWork();
                try {
                    BubbleWithEntityInCompositeComponent bubble = store.lock(mockupFactory.getWithNonNullComponentsId());
                    bubble.getLevel1Component().getEntitySet().clear();
                    store.update(bubble);
                    store.commitUnitOfWork(unitOfWork);
                } finally {
                    store.closeUnitOfWork(unitOfWork);
                }
                return null;
            }
        });
    }
}