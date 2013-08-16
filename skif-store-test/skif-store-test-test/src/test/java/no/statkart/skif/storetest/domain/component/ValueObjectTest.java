package no.statkart.skif.storetest.domain.component;


import com.google.inject.Inject;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;
import no.statkart.skif.storetest.domain.basic.BubbleWithValueObject;
import no.statkart.skif.storetest.mockup.BubbleWithValueObjectMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class ValueObjectTest extends StoreTestTestCase {
    @Inject
    Store store;
    @Inject
    StoreUpdateService storeUpdateService;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;


    public void testBubbleWithNullBeloep() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        final BubbleWithValueObject withNullBeloeb = store.get(valueObjectMockupFactory.getWithNullBeloepId());
        assertNull(withNullBeloeb.getA());
        assertNull(withNullBeloeb.getB());
    }

    public void testReadBubbleWithSameBeloep() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        final BubbleWithValueObject withSameBeloeb = store.get(valueObjectMockupFactory.getWithSameBeloepId());
        // Test at instaner ikke længre deles når de innleses via Store
        assertEquals(withSameBeloeb.getA(), valueObjectMockupFactory.getBeloepNOK1WithText());
        assertEquals(withSameBeloeb.getB(), valueObjectMockupFactory.getBeloepNOK1WithText());
        assertNotSame(withSameBeloeb.getA(), withSameBeloeb.getB());
    }

    /**
     * Tester oppdatering. Dette må skje ved å lage et nytt objekt da BeloepValueObject er immutable
     */
    public void testUpdateBeloep() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        store.beginUnitOfWork();
        BubbleWithValueObject bubbleWithBeloeb = store.lock(valueObjectMockupFactory.getWithSameBeloepId());

        bubbleWithBeloeb.setB(bubbleWithBeloeb.getB().withValuta("DKK").withKommentar("Changed valuta"));
        store.update(bubbleWithBeloeb);
        storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
        store.endUnitOfWork();

        BubbleWithValueObject withUpdatedBeloep = store.get(valueObjectMockupFactory.getWithSameBeloepId());
        assertEquals(bubbleWithBeloeb.getA(), valueObjectMockupFactory.getBeloepNOK1WithText());
        assertEquals(bubbleWithBeloeb.getB().getVerdi(), 1);
        assertEquals(withUpdatedBeloep.getB().getValuta(), "DKK");
        assertEquals(withUpdatedBeloep.getB().getKommentar(), "Changed valuta");
    }

    public void testReadBeloepSet() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        final BubbleWithValueObject withBeloepSet = store.get(valueObjectMockupFactory.getWithBeloepSetId());
        assertThat(withBeloepSet.getBeloepSet()).containsOnly(valueObjectMockupFactory.getBeloepDKR1(), valueObjectMockupFactory.getBeloepNOK1());
    }

    public void testUpdateBeloepSet() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        store.beginUnitOfWork();
        final BubbleWithValueObject bubbleWithBeloepSet = store.lock(valueObjectMockupFactory.getWithBeloepSetId());
        final BeloepValueObject beloepNOK = findValuta(bubbleWithBeloepSet.getBeloepSet(), "NOK");

        bubbleWithBeloepSet.getBeloepSet().remove(beloepNOK);
        bubbleWithBeloepSet.getBeloepSet().add(valueObjectMockupFactory.getBeloepSEK20());

        store.update(bubbleWithBeloepSet);
        storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
        store.endUnitOfWork();

        final BubbleWithValueObject updatedBubble = store.get(valueObjectMockupFactory.getWithBeloepSetId());
        assertThat(updatedBubble.getBeloepSet()).containsOnly(valueObjectMockupFactory.getBeloepDKR1(), valueObjectMockupFactory.getBeloepSEK20());
    }

    public void testUpdateKommentarInBeloepSet() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        store.beginUnitOfWork();
        final BubbleWithValueObject bubbleWithBeloepSet = store.lock(valueObjectMockupFactory.getWithBeloepSetId());
        final BeloepValueObject beloepNOK = findValuta(bubbleWithBeloepSet.getBeloepSet(), "NOK");

        bubbleWithBeloepSet.getBeloepSet().remove(beloepNOK);
        bubbleWithBeloepSet.getBeloepSet().add(beloepNOK.withVerdi(20).withKommentar("changed"));

        store.update(bubbleWithBeloepSet);
        storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
        store.endUnitOfWork();

        final BubbleWithValueObject updatedBubble = store.get(valueObjectMockupFactory.getWithBeloepSetId());
        assertThat(updatedBubble.getBeloepSet()).containsOnly(valueObjectMockupFactory.getBeloepDKR1(), new BeloepValueObject("NOK", 20, "changed"));
    }

    public void testAddExistingToBeloepSet() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        store.beginUnitOfWork();
        try {
            final BubbleWithValueObject bubbleWithBeloepSet = store.lock(valueObjectMockupFactory.getWithBeloepSetId());
            bubbleWithBeloepSet.getBeloepSet().add(new BeloepValueObject("NOK", 100, "Ekstra beløp i NOK"));
            store.update(bubbleWithBeloepSet);
            storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
            fail("Forvented database constraint exception");
        } catch (RuntimeException e) {
            store.abortUnitOfWork();
        }
    }

    private BeloepValueObject findValuta(Set<BeloepValueObject> withBeloepSet, String valuta) {
        for (BeloepValueObject beloep : withBeloepSet) {
            if (beloep.getValuta().equals(valuta)) {
                return beloep;
            }
        }
        return null;
    }

}