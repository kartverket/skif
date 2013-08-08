package no.statkart.skif.storetest.historikk;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.storetest.domain.basic.HistSimple;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Roar Ingebrigtsen
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class SnapshotTest extends StoreTestMixedTestCase {

    public void testHentObjectForForskjelligSnapshot() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            PersistenceSessionManager sessionManager;
            @Inject
            StoreTestMockupFacadeFactory mockupFacadeFactory;

            public Object run() {
                StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

                HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1();
                HistSimple HistSimpleCurrent = sessionManager.get(histSimpleId1);

                SnapshotVersion historiskSnapshot = SnapshotVersion.createInstance("2011-10-02 08:01:23.00");
                HistSimple HistSimpleHistorisk = sessionManager.get(histSimpleId1.asSnapshotVersion(historiskSnapshot));

                HistSimple HistSimpleOld =  sessionManager.get(histSimpleId1.asSnapshotVersion(SnapshotVersion.OLD));

                assertEquals(HistSimpleCurrent.getText(), "KARTVEIEN");
                assertEquals(HistSimpleHistorisk.getText(), "KARTVEGEN");
                assertEquals(HistSimpleOld.getText(), "KARTVEIEN");

                return null;
            }
        });
    }

}
