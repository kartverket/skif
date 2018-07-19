package no.statkart.skif.storetest.domain.component;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponent;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponentId;
import no.statkart.skif.storetest.domain.component.composite.Level1CompositeComponent;
import no.statkart.skif.storetest.domain.component.composite.Level2CompositeComponent;
import no.statkart.skif.storetest.mockup.BubbleWithCompositeComponentMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
@Test
public class CompositeComponentTest extends StoreTestTestCase {
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
                return mockupFacade.getBubbleWithCompositeComponentMockupFactory().getAllIds(BubbleWithCompositeComponentId.class);
            }
        });
    }

    public void testReadBubbleWithNullCompositeComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();
        final BubbleWithCompositeComponent bubbleWithNullComponents = store.get(mockupFactory.getWithNullComponentsId());
        assertEquals(bubbleWithNullComponents.getText(), "Obj " + 1 + " med null components");
        // Composite components som inneholder Set vil aldrig være null da de alltid vil ha en tomt Set.
        assertNotNull(bubbleWithNullComponents.getLevel1Component());
        assertNull(bubbleWithNullComponents.getLevel1Component().getText());
        assertNull(bubbleWithNullComponents.getLevel1Component().getBeloep());
        assertTrue(bubbleWithNullComponents.getLevel1Component().getBeloepSet().isEmpty());
        assertTrue(bubbleWithNullComponents.getLevel1Component().isNullComponent());
        assertNotNull(bubbleWithNullComponents.getLevel1Component().getLevel2Component());
        assertNull(bubbleWithNullComponents.getLevel1Component().getLevel2Component().getText());
        assertNull(bubbleWithNullComponents.getLevel1Component().getLevel2Component().getBeloep());
        assertTrue(bubbleWithNullComponents.getLevel1Component().getLevel2Component().getBeloepSet().isEmpty());
        assertTrue(bubbleWithNullComponents.getLevel1Component().getLevel2Component().isNullComponent());
    }

    public void testReadBubbleWithNonNullLevel1AndNullLevel2CompositeComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();
        final BubbleWithCompositeComponent bubbleWithNullLevel2Components = store.get(mockupFactory.getWithNullLevel2Id());

        assertEquals(bubbleWithNullLevel2Components.getText(), "Obj 2 med null level2 component");
        // Composite components som inneholder Set vil aldrig være null da de alltid vil ha en tomt Set.
        assertNotNull(bubbleWithNullLevel2Components.getLevel1Component());
        assertFalse(bubbleWithNullLevel2Components.getLevel1Component().isNullComponent());
        assertNotNull(bubbleWithNullLevel2Components.getLevel1Component().getText());
        assertNotNull(bubbleWithNullLevel2Components.getLevel1Component().getBeloep());
        assertFalse(bubbleWithNullLevel2Components.getLevel1Component().getBeloepSet().isEmpty());
//        assertNotNull(bubbleWithNullLevel2Components.getLevel1Component().getEntity());
        assertNotNull(bubbleWithNullLevel2Components.getLevel1Component().getLevel2Component());
        assertTrue(bubbleWithNullLevel2Components.getLevel1Component().getLevel2Component().isNullComponent());
        assertNull(bubbleWithNullLevel2Components.getLevel1Component().getLevel2Component().getText());
        assertNull(bubbleWithNullLevel2Components.getLevel1Component().getLevel2Component().getBeloep());
        assertTrue(bubbleWithNullLevel2Components.getLevel1Component().getLevel2Component().getBeloepSet().isEmpty());
    }

    public void testReadBubbleWithNonNullLevel1AndLevel2CompositeComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();
        final BubbleWithCompositeComponent bubbleWithNonNullComponents = store.get(mockupFactory.getWithNonNullComponentsId());

        assertEquals(bubbleWithNonNullComponents.getText(), "Obj 3 med level1 og level2 component");
        // Composite components som inneholder Set vil aldrig være null da de alltid vil ha en tomt Set.
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component());
        assertFalse(bubbleWithNonNullComponents.getLevel1Component().isNullComponent());
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component().getText());
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component().getBeloep());
        assertFalse(bubbleWithNonNullComponents.getLevel1Component().getBeloepSet().isEmpty());
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component().getLevel2Component());
        assertFalse(bubbleWithNonNullComponents.getLevel1Component().getLevel2Component().isNullComponent());
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component().getLevel2Component().getText());
        assertNotNull(bubbleWithNonNullComponents.getLevel1Component().getLevel2Component().getBeloep());
        assertFalse(bubbleWithNonNullComponents.getLevel1Component().getLevel2Component().getBeloepSet().isEmpty());
    }

    public void testSubstituteNullComponentWithNull() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            final BubbleWithCompositeComponent bubbleWithNullComponents = store.lock(mockupFactory.getWithNullComponentsId());
            bubbleWithNullComponents.setLevel1Component(null);
            storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
            store.endUnitOfWork(unitOfWork);
        } finally {
            store.closeUnitOfWork(unitOfWork);
        }

        final BubbleWithCompositeComponent bubbleWithCompositeComponent = store.get(mockupFactory.getWithNullComponentsId());
        assertTrue(bubbleWithCompositeComponent.getLevel1Component().isNullComponent());
    }


    public void testSubstituteNullComponentWithNonNull() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            final BubbleWithCompositeComponent bubbleWithCompositeComponent = store.lock(mockupFactory.getWithNullComponentsId());
            bubbleWithCompositeComponent.setLevel1Component(new Level1CompositeComponent());
            bubbleWithCompositeComponent.getLevel1Component().setText("I am not null");
            store.update(bubbleWithCompositeComponent);
            storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
            store.endUnitOfWork(unitOfWork);
        } finally {
            store.closeUnitOfWork(unitOfWork);
        }

        final BubbleWithCompositeComponent bubbleWithCompositeComponentSaved = store.get(mockupFactory.getWithNullComponentsId());
        assertFalse(bubbleWithCompositeComponentSaved.getLevel1Component().isNullComponent());
        assertTrue(bubbleWithCompositeComponentSaved.getLevel1Component().getLevel2Component().isNullComponent());
    }

    /**
     * Tester at det ikke er mulig å flytte en komponent fra et objekt til et annet. Skulle gjerne ønske at feilen
     * kom med en gang når man forsøker å sette komponenten slik at feilfindingen blir enklere. I nårværende
     * implementasjon oppdages feilen kun ved persistering til serveren.
     */
    public void testMoveComponent() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            final BubbleWithCompositeComponent bubbleWithCompositeComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
            Level1CompositeComponent existingLevel1Component = bubbleWithCompositeComponent.getLevel1Component();
            bubbleWithCompositeComponent.setLevel1Component(null);
            store.update(bubbleWithCompositeComponent);
            BubbleWithCompositeComponent newBubble = new BubbleWithCompositeComponent();
            newBubble.setLevel1Component(existingLevel1Component);
            store.insert(newBubble);
            try {
                storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
                failBecauseExceptionWasNotThrown(ImplementationException.class);
            } catch (ImplementationException e) {
                // Burde ikke være HibernateException en en SKIF exception. Det vil det være i JEE mode
                // PS: vi får kun feil her fordi komponenten inneholder et sett.
                assertThat(e).hasMessageContaining("Component contains a Collection that is null");
            }
        } finally {
            store.closeUnitOfWork(unitOfWork);
        }
    }

    /**
     * Tester at det ikke er mulig å flytte en nested komponent fra et objekt til et annet. Skulle gjerne ønske at feilen
     * kom med en gang når man forsøker å sette komponenten slik at feilfindingen blir enklere. I nårværende
     * implementasjon oppdages feilen kun ved persistering til serveren.
     */
    public void testMoveComponentLevel2() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            final BubbleWithCompositeComponent bubbleWithCompositeComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
            Level2CompositeComponent existingLevel2Component = bubbleWithCompositeComponent.getLevel1Component().getLevel2Component();
            bubbleWithCompositeComponent.getLevel1Component().setLevel2Component(null);
            store.update(bubbleWithCompositeComponent);
            BubbleWithCompositeComponent newBubble = new BubbleWithCompositeComponent();
            newBubble.setLevel1Component(new Level1CompositeComponent());
            newBubble.getLevel1Component().setLevel2Component(existingLevel2Component);
            store.insert(newBubble);
            try {
                storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
                failBecauseExceptionWasNotThrown(ImplementationException.class);
            } catch (ImplementationException e) {
                // Burde ikke være HibernateException en en SKIF exception. Det vil det være i JEE mode
                // PS: vi får kun feil her fordi komponenten inneholder et sett.
                assertThat(e).hasMessageContaining("Component contains a Collection that is null");
            }
        } finally {
            store.closeUnitOfWork(unitOfWork);
        }
    }

    /**
     * Tester at det ikke er mulig å dele en komponent
     */
    public void testShareComponent() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithCompositeComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithCompositeComponentMockupFactory();

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            final BubbleWithCompositeComponent bubbleWithCompositeComponent = store.lock(mockupFactory.getWithNullComponentsId());

            Level1CompositeComponent sharedLevel1Component = new Level1CompositeComponent();
            bubbleWithCompositeComponent.setLevel1Component(sharedLevel1Component);

            BubbleWithCompositeComponent newBubble = new BubbleWithCompositeComponent();
            try {
                newBubble.setLevel1Component(sharedLevel1Component);
                fail();
            } catch (IllegalStateException e) {
                // forventet
            }
        } finally {
            store.closeUnitOfWork(unitOfWork);
        }
    }
}