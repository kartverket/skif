package no.statkart.skif.storetest.domain.component;


import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;
import no.statkart.skif.storetest.domain.basic.BubbleWithValueObject;
import no.statkart.skif.storetest.domain.basic.BubbleWithValueObjectId;
import no.statkart.skif.storetest.mockup.BubbleWithValueObjectMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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
        Assert.assertNull(withNullBeloeb.getA());
        Assert.assertNull(withNullBeloeb.getB());
    }

    public void testReadBubbleWithSameBeloep() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        final BubbleWithValueObject withSameBeloeb = store.get(valueObjectMockupFactory.getWithSameBeloepId());
        // Test at instaner ikke længre deles når de innleses via Store
        Assert.assertEquals(withSameBeloeb.getA(), valueObjectMockupFactory.getBeloepNOK1WithText());
        Assert.assertEquals(withSameBeloeb.getB(), valueObjectMockupFactory.getBeloepNOK1WithText());
        Assert.assertNotSame(withSameBeloeb.getA(), withSameBeloeb.getB());
    }

    /**
     * Tester oppdatering. Dette må skje ved å lage et nytt objekt da BeloepValueObject er immutable
     */
    public void testUpdateBeloep() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        BubbleWithValueObject bubbleWithBeloeb;
        try {
            bubbleWithBeloeb = store.lock(valueObjectMockupFactory.getWithSameBeloepId());

            bubbleWithBeloeb.setB(bubbleWithBeloeb.getB().withValuta("DKK").withKommentar("Changed valuta"));
            store.update(bubbleWithBeloeb);
            storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
            store.endUnitOfWork(unitOfWork);
        } finally {
            store.closeUnitOfWork(unitOfWork);
        }

        BubbleWithValueObject withUpdatedBeloep = store.get(valueObjectMockupFactory.getWithSameBeloepId());
        Assert.assertEquals(bubbleWithBeloeb.getA(), valueObjectMockupFactory.getBeloepNOK1WithText());
        Assert.assertEquals(bubbleWithBeloeb.getB().getVerdi(), 1);
        Assert.assertEquals(withUpdatedBeloep.getB().getValuta(), "DKK");
        Assert.assertEquals(withUpdatedBeloep.getB().getKommentar(), "Changed valuta");
    }

    public void testReadBeloepSet() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();
        final BubbleWithValueObject withBeloepSet = store.get(valueObjectMockupFactory.getWithBeloepSetId());
        assertThat(withBeloepSet.getBeloepSet()).containsOnly(valueObjectMockupFactory.getBeloepDKR1(), valueObjectMockupFactory.getBeloepNOK1());
    }

    public void testReadLazyBeloepSet() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();

        final ImmutableSet<BubbleWithValueObjectId<?>> of = ImmutableSet.of(valueObjectMockupFactory.getWithBeloepSetId(), valueObjectMockupFactory.getWithBeloepSetId2());
        final Set<BubbleWithValueObject> bubbleWithValueObjects = store.get(of);
        assertThat(bubbleWithValueObjects).hasSize(2);
    }

    public void testUpdateBeloepSet() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            final BubbleWithValueObject bubbleWithBeloepSet = store.lock(valueObjectMockupFactory.getWithBeloepSetId());
            final BeloepValueObject beloepNOK = findValuta(bubbleWithBeloepSet.getBeloepSet(), "NOK");

            bubbleWithBeloepSet.getBeloepSet().remove(beloepNOK);
            bubbleWithBeloepSet.getBeloepSet().add(valueObjectMockupFactory.getBeloepSEK20());

            store.update(bubbleWithBeloepSet);
            storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
            store.endUnitOfWork(unitOfWork);
        } finally {
            store.closeUnitOfWork(unitOfWork);
        }

        final BubbleWithValueObject updatedBubble = store.get(valueObjectMockupFactory.getWithBeloepSetId());
        assertThat(updatedBubble.getBeloepSet()).containsOnly(valueObjectMockupFactory.getBeloepDKR1(), valueObjectMockupFactory.getBeloepSEK20());
    }

    public void testUpdateKommentarInBeloepSet() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            final BubbleWithValueObject bubbleWithBeloepSet = store.lock(valueObjectMockupFactory.getWithBeloepSetId());
            final BeloepValueObject beloepNOK = findValuta(bubbleWithBeloepSet.getBeloepSet(), "NOK");

            bubbleWithBeloepSet.getBeloepSet().remove(beloepNOK);
            bubbleWithBeloepSet.getBeloepSet().add(beloepNOK.withVerdi(20).withKommentar("changed"));

            store.update(bubbleWithBeloepSet);
            storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
            store.endUnitOfWork(unitOfWork);
        } finally {
            store.closeUnitOfWork(unitOfWork);
        }

        final BubbleWithValueObject updatedBubble = store.get(valueObjectMockupFactory.getWithBeloepSetId());
        assertThat(updatedBubble.getBeloepSet()).containsOnly(valueObjectMockupFactory.getBeloepDKR1(), new BeloepValueObject("NOK", 20, "changed"));
    }

    public void testAddExistingToBeloepSet() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        final BubbleWithValueObjectMockupFactory valueObjectMockupFactory = mockupFacade.getBubbleWithValueObjectMockupFactory();

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            final BubbleWithValueObject bubbleWithBeloepSet = store.lock(valueObjectMockupFactory.getWithBeloepSetId());
            bubbleWithBeloepSet.getBeloepSet().add(new BeloepValueObject("NOK", 100, "Ekstra beløp i NOK"));
            store.update(bubbleWithBeloepSet);
            storeUpdateService.saveTransfer(store.getUnitOfWorkTransfer());
            Assert.fail("Expected exception due to database constraint");
        } catch (ImplementationException e) {
            assertThat(e.getMessage()).isEqualTo("could not execute batch");
        } finally {
            store.closeUnitOfWork(unitOfWork);
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