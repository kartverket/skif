package no.statkart.skif.storetest.persistence;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Injector;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;
import no.statkart.skif.storetest.domain.basic.BubbleWithValueObject;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityInCompositeComponent;
import no.statkart.skif.storetest.domain.component.entity.Level1SetEntityInCompositeComponent;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Set;

/**
 * Tester at vi fanger opp dersom et objekt blir endret og flushet, men uten at Store blir informert om det.
 *
 * @since 2.5.1
 */
public class FlushNoUpdateTest extends StoreTestServerTestCase {

    private StoreTestMockupFacade mockupFacade;

    @BeforeClass
    public void saveData() {
        Injector clientInjector = getClientInjector();
        resetLogin();
        RunOnServerWithTxRequiresNewService runOnServerService = clientInjector.getInstance(RunOnServerWithTxRequiresNewService.class);
        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private StoreTestMockupFacadeFactory mockupFacadeFactory;

            @Override
            public Object run() {
                mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<StoreTestMockupFacade>() {
                    @Override
                    public Set<? extends BubbleId> selectFrom(StoreTestMockupFacade mockupFacade) {
                        return ImmutableSet.of(
                                mockupFacade.getBubbleWithValueObjectMockupFactory().getWithBeloepSetId(),
                                mockupFacade.getBubbleWithCompositeComponentMockupFactory().getWithNonNullComponentsId(),
                                mockupFacade.getBubbleWithEntityComponentMockupFactory().getWithNonNullComponentsId(),
                                mockupFacade.getBubbleWithEntityInCompositeComponentMockupFactory().getWithNonNullComponentsId()
                        );
                    }
                });
                return null;
            }
        });
    }

    @Inject
    private Store store;

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Modified object not updated!.+")
    public void testUpdateBubble() {
        BubbleWithValueObject object = store.get(mockupFacade.getBubbleWithValueObjectMockupFactory().getWithBeloepSetId());
        object.setText("Jeg er endret!");
    }

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Modified object not updated!.+")
    public void testUpdateValueComponent() {
        BubbleWithValueObject object = store.get(mockupFacade.getBubbleWithValueObjectMockupFactory().getWithBeloepSetId());
        object.setA(new BeloepValueObject("SEK", 123));
    }

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Modified object not updated!.+")
    public void testUpdateSetOfValueComponents() {
        BubbleWithValueObject object = store.get(mockupFacade.getBubbleWithValueObjectMockupFactory().getWithBeloepSetId());
        object.getBeloepSet().add(new BeloepValueObject("SEK", 321));
    }

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Modified object not updated!.+")
    public void testUpdateCompositeComponent1() {
        BubbleWithCompositeComponent object = store.get(mockupFacade.getBubbleWithCompositeComponentMockupFactory().getWithNonNullComponentsId());
        //noinspection ConstantConditions
        object.getLevel1Component().setText("Jeg er endret!");
    }

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Modified object not updated!.+")
    public void testUpdateCompositeComponent2() {
        BubbleWithCompositeComponent object = store.get(mockupFacade.getBubbleWithCompositeComponentMockupFactory().getWithNonNullComponentsId());
        //noinspection ConstantConditions
        object.getLevel1Component().getLevel2Component().setText("Jeg er endret!");
    }

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Modified object not updated!.+")
    public void testUpdateEntityComponent1() {
        BubbleWithEntityComponent object = store.get(mockupFacade.getBubbleWithEntityComponentMockupFactory().getWithNonNullComponentsId());
        //noinspection ConstantConditions
        object.getLevel1Component().setText("Jeg er endret!");
    }

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Modified object not updated!.+")
    public void testUpdateEntityComponent2() {
        BubbleWithEntityComponent object = store.get(mockupFacade.getBubbleWithEntityComponentMockupFactory().getWithNonNullComponentsId());
        //noinspection ConstantConditions
        object.getLevel1Component().getLevel2Component().setText("Jeg er endret!");
    }

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Modified object not updated!.+")
    public void testUpdateEntityComponentInCompositeComponent1() {
        BubbleWithEntityInCompositeComponent object = store.get(mockupFacade.getBubbleWithEntityInCompositeComponentMockupFactory().getWithNonNullComponentsId());
        //noinspection ConstantConditions
        object.getLevel1Component().getEntity().setText("Jeg er endret!");
    }

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Modified object not updated!.+")
    public void testUpdateEntityComponentInCompositeComponent2() {
        BubbleWithEntityInCompositeComponent object = store.get(mockupFacade.getBubbleWithEntityInCompositeComponentMockupFactory().getWithNonNullComponentsId());
        //noinspection ConstantConditions
        object.getLevel1Component().getEntitySet().iterator().next().setText("Jeg er endret!");
    }

    @Test(expectedExceptions = ImplementationException.class, expectedExceptionsMessageRegExp = "Modified object not updated!.+")
    public void testUpdateSetOfEntityComponentInCompositeComponent() {
        BubbleWithEntityInCompositeComponent object = store.get(mockupFacade.getBubbleWithEntityInCompositeComponentMockupFactory().getWithNonNullComponentsId());
        //noinspection ConstantConditions
        object.getLevel1Component().getEntitySet().add(new Level1SetEntityInCompositeComponent("Skal ikke virke!"));
    }
}
