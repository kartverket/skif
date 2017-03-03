package no.statkart.skif.standalone.store.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMasterImpl;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponentId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import no.statkart.skif.util.CopyHelper;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Dette er ting som ikke kan testes med det som er tilgjengelig for HibernatePersistenceSessionTest da de krever
 * mer kompliserte data fra mockup.
 */
@Test(groups = "singlevm-required")
public class HibernatePersistenceSessionTest2 extends StoreTestServerTestCase {

    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private ResourceManager resourceManager;

    public void testLazyJoinFetch() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        PersistenceSessionManager persistenceSessionManager = resourceManager.getResource(PersistenceSessionManager.class);
        HibernatePersistenceSessionMasterImpl hibernatePersistenceSessionMaster = persistenceSessionManager.getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(HibernatePersistenceSessionMasterImpl.class);

        // Den viktige forutsetningen her er at get() skal sørge for at det den returnerer ikke inneholder noe lazy
        Assert.assertFalse(hibernatePersistenceSessionMaster.isLazyLoadedBubblesAllowed());

        BubbleWithEntityComponentId<?> id = mockupFacade.getBubbleWithEntityComponentMockupFactory().getWithNullLevel2Id();

        Session session = hibernatePersistenceSessionMaster.reserveSession();
        try {
            session.createCriteria(BubbleWithEntityComponent.class)
                    .add(Restrictions.idEq(id))
                    .setFetchMode("level1Component", FetchMode.SELECT)
                    .list();

            BubbleWithEntityComponent bubble = hibernatePersistenceSessionMaster.get(id);
            //noinspection ConstantConditions
            CopyHelper.copy(bubble).getLevel1Component().getBeloepSet().size();
        } finally {
            hibernatePersistenceSessionMaster.releaseSession();
        }
    }
}
