package no.statkart.skif.storetest.domain.component;


import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.storetest.domain.component.entity.*;
import no.statkart.skif.storetest.mockup.BubbleWithEntityComponentMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.hibernate.Session;
import org.testng.annotations.Test;

import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Fail.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.*;

/**
 * Tester bruk av EntityComponent på Serveren for attached og detached state for one-to-many mappings
 * <p/>
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
@Test(groups = {"singlevm-required","hibernate36"})
public class EntityComponentOneToManyServerTest extends StoreTestMixedTestCase {
    // Switch som angir om testcasen skal teste med flush etter hver endring. Har valgt ikke å har egne
    // testcases for hver mode for å redusere antall testcases som skal kjøres og vedlikeholdes
    final boolean flushing = false;

    @Inject
    Store store;
    @Inject
    StoreUpdateService storeUpdateService;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;
    @Inject
    TestdataService testdataService;

    private StoreTestMockupFacade getWriteMockupFacadeAndSaveDataForTestSet1() {
        return mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<StoreTestMockupFacade>() {
            @Override
            public Set<? extends BubbleId> selectFrom(StoreTestMockupFacade mockupFacade) {
                return mockupFacade.getBubbleWithEntityComponentMockupFactory().getAllIds(BubbleWithEntityComponentId.class);
            }
        });
    }


    public void testDeleteComponentFromSetInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneSetAaComponentsId2());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneSetAaComponentsId2());
                bubble.getAaComponents().clear();
                if (flushing) store.flush();
                store.update(bubble);
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneSetAaComponentsId2());
        assertThat(changedBubble.getAaComponents()).hasSize(0);
        assertFalse(existsInDatabase(SetAaEntityComponent.class.getName(), originalBubble.getAaComponents().iterator().next().getId()));
    }

    public void testAddComponentToSetInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneSetAaComponentsId2());
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneSetAaComponentsId2());
                bubble.getAaComponents().add(new SetAaEntityComponent(mockupFactory.getNextIdent(), "Component i " + bubble.getNr()));
                if (flushing) store.flush();
                store.update(bubble);
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneSetAaComponentsId2());
        assertThat(changedBubble.getAaComponents()).hasSize(2);

        // TODO: Verifisert at owner er satt på komponent
    }

    public void testAddAndRemoveComponentsInSetInAttachedState() {
        final StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final BubbleWithEntityComponentMockupFactory mockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();
        final BubbleWithEntityComponent originalBubble = store.get(mockupFactory.getWithOneSetAaComponentsId2());
        final SetAaEntityComponent componentToRemove = originalBubble.getAaComponents().iterator().next();
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                final BubbleWithEntityComponent bubble = store.lock(mockupFactory.getWithOneSetAaComponentsId2());
                bubble.getAaComponents().add(new SetAaEntityComponent(mockupFactory.getNextIdent(),"Component i " + bubble.getNr()));
                bubble.getAaComponents().remove(componentToRemove);
                if (flushing) store.flush();                store.update(bubble);
                return null;
            }
        });
        final BubbleWithEntityComponent changedBubble = store.get(mockupFactory.getWithOneSetAaComponentsId2());
        assertThat(changedBubble.getAaComponents()).hasSize(1);
        assertThat(changedBubble.getAaComponents()).doesNotContain(componentToRemove);

        // TODO: Verifisert at owner er satt på komponent
    }

    private boolean existsInDatabase(final String className, final Long id) {
        return (Boolean) server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Session session;

            public Object run() {
                Long count = (Long) session.createQuery(String.format("select count(*) from %s where id=:id", className))
                        .setLong("id", id)
                        .uniqueResult();
                return count > 0;
            }
        });
    }
}