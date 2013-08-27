package no.statkart.skif.storetest.domain.component;


import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponentId;
import no.statkart.skif.storetest.domain.component.entity.Level1EntityComponent;
import no.statkart.skif.storetest.mockup.BubbleWithEntityComponentMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.SortedMap;

import static org.fest.assertions.api.Fail.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.*;

/**
 *  Tester EntityCompontent i UnitOfWork på Klient for SingleVm og JEE mode
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
@Test
public class EntityComponentTest extends StoreTestTestCase {
    @Inject
    Store store;
    @Inject
    StoreUpdateService storeUpdateService;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;
    @Inject
    TestdataService testdataService;


    /**
     * Tester at Hibernate kan batch sql for opprettelse bobler som inneholder EntityComponents. Denne test må
     * kjøres med hibernate sql og batch logging satt til debug og det er nødvendig manuelt å sjekke at all
     * sql blir batchet. Det som må sjekkes er at sql statements ikke forekommer ut av sekvens, dvs. at alle like sql
     * statements kommer rett etter hverandre og ingen andre steder.
     */
    public void testBatchInsert() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        SortedMap<SnapshotVersion,MockupTransfer> allTransfersForIds = mockupFacade.getAllTransfersForIds(
                ImmutableSet.of(
                        mockupFactory.getWithNonNullComponentsId(),
                        mockupFactory.getWithNullComponentsId(),
                        mockupFactory.getWithNonNullComponentsId2(),
                        mockupFactory.getWithNullLevel2Id())
        );
        testdataService.saveAll(allTransfersForIds);
    }

    /**
     * Tester at Hibernate kan batch laste bobler som inneholder EntityComponents effektiv. Denne test må kjøres med
     * hibernate sql og batch logging satt til debug og det er nødvendig manuelt å sjekke at all sql som utføres
     * ved lastingen anvender liste av id'er som parameter slik at sql'en utføres samlet for alle objekter og ikke
     * per objekt. Dette skal også gjelder for collections.
     * <P>
     * Et lille triks er å kjøre testen flere ganger slik at readsettet allerede er opprettet når sql'en inspiseres.
     */
    public void testBatchRead() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        ImmutableSet<BubbleWithEntityComponentId<?>> ids = ImmutableSet.of(
                mockupFactory.getWithNonNullComponentsId(),
                mockupFactory.getWithNullComponentsId(),
                mockupFactory.getWithNonNullComponentsId2(),
                mockupFactory.getWithNullLevel2Id());
        Set<BubbleWithEntityComponent> bubbleWithEntityComponents = store.get(ids);
        assertEquals(bubbleWithEntityComponents.size(), 4);
    }

    public void testReadBubbleWithNullEntityComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubbleWithNullComponents = store.get(mockupFactory.getWithNullComponentsId());
        assertEquals(bubbleWithNullComponents.getText(), "Obj " + 1 + " med null components");
        assertNull(bubbleWithNullComponents.getLevel1Component());
    }

    public void testReadBubbleWithNonNullLevel1AndNullLevel2EntityComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubble = store.get(mockupFactory.getWithNullLevel2Id());
        assertEquals(bubble.getNr(), 2);
        assertEquals(bubble.getText(), "Obj " + 2 + " med null level2 component");
        assertNotNull(bubble.getLevel1Component());
        assertNull(bubble.getLevel1Component().getLevel2Component());
    }

    public void testReadBubbleWithNonNullLevel1AndLevel2EntityComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent bubble = store.get(mockupFactory.getWithNonNullComponentsId());
        assertEquals(bubble.getText(), "Obj " + 3 + " med level1 og level2 components");
        assertNotNull(bubble.getLevel1Component());
        assertNotNull(bubble.getLevel1Component().getLevel2Component());
    }

    public void testSubstituteNullComponentWithNull() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        store.beginUnitOfWork();
        final BubbleWithEntityComponent bubbleWithNullComponents = store.lock(mockupFactory.getWithNullComponentsId());
        assertNull(bubbleWithNullComponents.getLevel1Component());
        bubbleWithNullComponents.setLevel1Component(null);
        storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
        store.endUnitOfWork();

        final BubbleWithEntityComponent updatedBubble = store.get(mockupFactory.getWithNullComponentsId());
        assertNull(updatedBubble.getLevel1Component());
    }

    public void testSubstituteNullComponentWithNonNull() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        store.beginUnitOfWork();
        final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNullComponentsId());
        bubbleWithEntityComponent.setLevel1Component(new Level1EntityComponent());
        bubbleWithEntityComponent.getLevel1Component().setText("I am not null");
        store.update(bubbleWithEntityComponent);
        storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
        store.endUnitOfWork();

        final BubbleWithEntityComponent bubbleWithEntityComponentSaved = store.get(mockupFactory.getWithNullComponentsId());
        assertNotNull(bubbleWithEntityComponentSaved.getLevel1Component());
        assertEquals(bubbleWithEntityComponentSaved.getLevel1Component().getText(), "I am not null");
        assertNull(bubbleWithEntityComponentSaved.getLevel1Component().getLevel2Component());
    }

    /**
     * Tester at det ikke er mulig å flytte en komponent fra et objekt til et annet. Skulle gjerne ønske at feilen
     * kom med en gang når man forsøker å sette komponenten slik at feilfindingen blir enklere. I nårværende
     * implementasjon oppdages feilen kun ved persistering til serveren.
     */
    public void testMoveComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        store.beginUnitOfWork();
        final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNonNullComponentsId());
        Level1EntityComponent existingLevel1Component = bubbleWithEntityComponent.getLevel1Component();
        bubbleWithEntityComponent.setLevel1Component(null);
        store.update(bubbleWithEntityComponent);
        BubbleWithEntityComponent newBubble = new BubbleWithEntityComponent();
        try {
            newBubble.setLevel1Component(existingLevel1Component);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException t) {
            // OK, forventet
            assertTrue(t.getMessage().startsWith("Attempt to assign component to a new owner"));
        }
        store.abortUnitOfWork();
    }
    
    public void testShareComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        store.beginUnitOfWork();
        final BubbleWithEntityComponent bubbleWithEntityComponent = store.lock(mockupFactory.getWithNullComponentsId());

        Level1EntityComponent sharedLevel1Component = new Level1EntityComponent();
        bubbleWithEntityComponent.setLevel1Component(sharedLevel1Component);

        BubbleWithEntityComponent newBubble = new BubbleWithEntityComponent();
        try {
            newBubble.setLevel1Component(sharedLevel1Component);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            // forventet
        }
        store.abortUnitOfWork();
    }

    @Test(enabled = false) // TODO: Det er pt ikke mulig å fjerne en entity component som en boble peker på. Avventer fix
    public void testDeleteComponent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        store.beginUnitOfWork();
        final BubbleWithEntityComponent bubbleWithLevel1Component = store.lock(mockupFactory.getWithNullLevel2Id());
        bubbleWithLevel1Component.setLevel1Component(null);
        store.update(bubbleWithLevel1Component);
        storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
        store.endUnitOfWork();

        // TODO: skrive databasekode som sjekker at level1 og level2 komponeter er slettet.
        final BubbleWithEntityComponent updatedBubble = store.lock(mockupFactory.getWithNullLevel2Id());
        assertNull(updatedBubble.getLevel1Component());
    }

    @Test(enabled = false) // TODO: Det er pt ikke mulig å fjerne en entity component som en boble peker på. Avventer fix
    public void testDeleteComponentLevel1AndLevel2() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        store.beginUnitOfWork();
        final BubbleWithEntityComponent bubbleWithLevel1AndLevel2Components = store.lock(mockupFactory.getWithNonNullComponentsId());
        bubbleWithLevel1AndLevel2Components.getLevel1Component().setLevel2Component(null);
        bubbleWithLevel1AndLevel2Components.setLevel1Component(null);
        store.update(bubbleWithLevel1AndLevel2Components);
        storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
        store.endUnitOfWork();

        // TODO: skrive databasekode som sjekker at level1 og level2 komponeter er slettet.
        final BubbleWithEntityComponent updatedBubble = store.lock(mockupFactory.getWithNullLevel2Id());
        assertNull(updatedBubble.getLevel1Component());
    }
}