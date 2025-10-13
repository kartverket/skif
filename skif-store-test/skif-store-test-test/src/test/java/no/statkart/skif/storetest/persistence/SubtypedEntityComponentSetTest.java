package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithSubtypedEntityComponentSet;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithSubtypedEntityComponentSetId;
import no.statkart.skif.storetest.domain.component.entity.Subtype1EntityComponentForSet;
import no.statkart.skif.storetest.domain.component.entity.Subtype2EntityComponentForSet;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.CopyHelper;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.util.Collections;

/**
 * Tester endring av subtype på entitycomponent på tjenersiden.
 */
public class SubtypedEntityComponentSetTest extends StoreTestTestCase {
    private final long NON_DEFAULT_NR = 1;

    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private RunOnServerWithTxRequiresNewService server;

    @Inject
    private Store clientStore;

    @Test(groups = {"singlevm-required"})
    public void enkelLesetest() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        server.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithSubtypedEntityComponentSetId<?> bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentSetMockupFactory().getWithNonNullSubtypedComponentsId();
                BubbleWithSubtypedEntityComponentSet current = store.get(bubbleId);
                Assertions.assertThat(current.getSubtypedEntityComponentSet().size() == 1).isTrue();
                Assertions.assertThat(current.getSubtypedEntityComponentSet().iterator().next() instanceof Subtype1EntityComponentForSet).isTrue();
                return null;
            }
        });
    }

    @Test(groups = {"singlevm-required"})
    public void updateWithSubtypeChangeShouldThrowWithUnitOfWork() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                UnitOfWork uow = store.beginUnitOfWork();
                try {
                    BubbleWithSubtypedEntityComponentSetId<?> bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentSetMockupFactory().getWithNonNullSubtypedComponentsId();
                    BubbleWithSubtypedEntityComponentSet bubble = store.lock(bubbleId);

                    Subtype1EntityComponentForSet oldComponent = (Subtype1EntityComponentForSet) bubble.getSubtypedEntityComponentSet().iterator().next();
                    Subtype2EntityComponentForSet newComponentWithOldId = new Subtype2EntityComponentForSet();
                    newComponentWithOldId.setId(oldComponent.getId());
                    newComponentWithOldId.setNr(NON_DEFAULT_NR);

                    bubble.setSubtypedEntityComponentSet(Collections.singletonList(newComponentWithOldId));
                    store.update(bubble);

                    Assertions.assertThatThrownBy(() -> store.commitUnitOfWork(uow))
                        .isInstanceOf(ImplementationException.class)
                        .hasMessageContaining("Attempted to change class");
                } finally {
                    uow.close();
                }
                return null;
            }
        });

    }

    @Test(groups = {"singlevm-required"})
    public void updateWithSubtypeChangeShouldThrowWithCopy() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();

        server.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {

                BubbleWithSubtypedEntityComponentSetId<?> bubbleId = mockupFacade.getBubbleWithSubtypedEntityComponentSetMockupFactory().getWithNonNullSubtypedComponentsId();
                BubbleWithSubtypedEntityComponentSet bubble = store.lock(bubbleId);
                BubbleWithSubtypedEntityComponentSet bubbleCopy = CopyHelper.copy(bubble);

                Subtype1EntityComponentForSet oldComponent = (Subtype1EntityComponentForSet) bubble.getSubtypedEntityComponentSet().iterator().next();
                Subtype2EntityComponentForSet newComponentWithOldId = new Subtype2EntityComponentForSet();
                newComponentWithOldId.setId(oldComponent.getId());
                newComponentWithOldId.setNr(NON_DEFAULT_NR);

                bubbleCopy.setSubtypedEntityComponentSet(Collections.singletonList(newComponentWithOldId));

                Assertions.assertThatThrownBy(() ->
                        store.update(bubbleCopy))
                    .isInstanceOf(ImplementationException.class)
                    .hasMessageContaining("Attempted to change class");

                return null;
            }
        });

    }

}
