package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponentId;
import no.statkart.skif.storetest.mockup.BubbleWithEntityComponentMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.testng.annotations.Test;

/**
 * Test re-attaching av bobler.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.3
 */
@Test(groups = "singlevm-required")
public class ReattachmentTest extends StoreTestServerTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private Store store;

    @Inject
    private ResourceManager resourceManager;

    public void testLazyCollections() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        BubbleWithEntityComponentMockupFactory bubbleWithEntityComponentMockupFactory = mockupFacade.getBubbleWithEntityComponentMockupFactory();

        BubbleWithEntityComponentId<?> id = bubbleWithEntityComponentMockupFactory.getWithOneAaComponentInSetId();

        PersistenceSessionManager persistenceSessionManager = resourceManager.getResource(PersistenceSessionManager.class);
        HibernatePersistenceSessionMasterImpl persistenceSessionMaster = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(HibernatePersistenceSessionMasterImpl.class);
        persistenceSessionMaster.setLazyLoadedBubblesAllowed(true);

        store.beginUnitOfWork();
        boolean ok = false;
        try {
            BubbleWithEntityComponent bubble = store.lock(id);

            store.update(bubble);
            store.commitUnitOfWork();
            ok = true;
        } finally {
            if (!ok) {
                store.abortUnitOfWork();
            }
        }
    }
}
