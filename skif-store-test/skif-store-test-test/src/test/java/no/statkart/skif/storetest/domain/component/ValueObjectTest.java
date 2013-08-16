package no.statkart.skif.storetest.domain.component;


import com.google.inject.Inject;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.basic.BubbleWithValueObject;
import no.statkart.skif.storetest.mockup.BubbleWithValueObjectMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

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

    public void testReadBubbleWithSharedBeloep() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        final BubbleWithValueObject withSharedBeloeb = store.get(valueObjectMockupFactory.getWithNullBeloepId());
        // Test at instaner ikke længre deles når de innleses via Store
        assertEquals(withSharedBeloeb.getA().getValuta(), "NOK");
        assertEquals(withSharedBeloeb.getA().getVerdi(), 1);
        assertEquals(withSharedBeloeb.getA().getKommentar(), "");
        assertEquals(withSharedBeloeb.getB().getValuta(), "NOK");
        assertEquals(withSharedBeloeb.getB().getVerdi(), 1);
        assertEquals(withSharedBeloeb.getB().getKommentar(), "");
        assertEquals(withSharedBeloeb.getA(), withSharedBeloeb.getB());
        assertNotSame(withSharedBeloeb.getA(), withSharedBeloeb.getB());
    }

    /**
     * Tester oppdatering. Dette må skje ved å lage et nytt objekt da BeloepValueObject er immutable
     */
    public void testUpdateBeloep() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        store.beginUnitOfWork();
        BubbleWithValueObject withSharedBeloeb = store.lock(valueObjectMockupFactory.getWithSharedBeloepId());
        assertEquals(withSharedBeloeb.getA().getVerdi(), 1);
        assertEquals(withSharedBeloeb.getA().getValuta(), "NOK");
        assertEquals(withSharedBeloeb.getA().getKommentar(), "I have text");
        assertEquals(withSharedBeloeb.getB().getVerdi(), 1);
        assertEquals(withSharedBeloeb.getB().getValuta(), "NOK");
        assertEquals(withSharedBeloeb.getB().getKommentar(), "I have text");

        withSharedBeloeb.setB(withSharedBeloeb.getB().withValuta("DKK").withKommentar("Changed valuta"));
        store.update(withSharedBeloeb);
        storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
        store.endUnitOfWork();

        BubbleWithValueObject withSharedBeloeb2 = store.get(valueObjectMockupFactory.getWithSharedBeloepId());
        assertEquals(withSharedBeloeb.getA().getValuta(), "NOK");
        assertEquals(withSharedBeloeb.getB().getVerdi(), 1);
        assertEquals(withSharedBeloeb2.getB().getValuta(), "DKK");
        assertEquals(withSharedBeloeb2.getB().getKommentar(), "Changed valuta");
    }

    public void testUpdateBeloepSet() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        store.beginUnitOfWork();
        final BubbleWithValueObject withBeloepSet = store.lock(valueObjectMockupFactory.getWithBeloepSetId());
        assertThat(withBeloepSet.getBeloepSet()).containsExactly(valueObjectMockupFactory.getBeloepDKK1(), valueObjectMockupFactory.getBeloepNOK1());
        withBeloepSet.getBeloepSet().remove(valueObjectMockupFactory.getBeloepNOK1());
        withBeloepSet.getBeloepSet().add(valueObjectMockupFactory.getBeloepSKR1());
        store.update(withBeloepSet);
        storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
        store.endUnitOfWork();

        final BubbleWithValueObject withBeloepSetChanged = store.get(valueObjectMockupFactory.getWithBeloepSetId());
        assertThat(withBeloepSet.getBeloepSet()).containsExactly(valueObjectMockupFactory.getBeloepDKK1(), valueObjectMockupFactory.getBeloepSKR1());
    }
}