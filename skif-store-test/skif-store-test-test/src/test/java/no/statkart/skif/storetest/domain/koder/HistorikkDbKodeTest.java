package no.statkart.skif.storetest.domain.koder;

import com.google.inject.Inject;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.store.service.LockService;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.JDBCHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Tester historikk på databasekoder.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Test(groups = "singlevm-required")
public class HistorikkDbKodeTest extends StoreTestTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private StoreService storeService;

    @Inject
    private LockService lockService;

    @Inject
    private StoreUpdateService updateService;

    public void testInsertUpdateRemoveDbKode() {
        SimpleLocalizedDbKodeId kodeId = prepareId();

        try {
            SimpleLocalizedDbKode kodeForInsert = new SimpleLocalizedDbKode();
            kodeForInsert.setId(kodeId);
            kodeForInsert.setKodeverdi("ABC");
            UnitOfWorkTransfer insertTransfer = new UnitOfWorkTransfer(Collections.<BubbleObject>singletonList(kodeForInsert), Collections.emptyList(), Collections.emptyList());
            updateService.saveTransfer(insertTransfer);

            StoreTestKodelisteLong kodeliste1 = storeService.getObject(SimpleLocalizedDbKodeId.KODELISTE_ID);
            Assert.assertEquals(kodeliste1.getKoderIds(), Collections.singletonList(kodeId));

            List<SimpleLocalizedDbKodeId> postInsertVersions = storeService.getVersions(kodeId, SnapshotVersion.START, SnapshotVersion.CURRENT);
            Assert.assertEquals(postInsertVersions.size(), 1, "Antall historikkinnslag etter opprettelse");

            SimpleLocalizedDbKode kodeForUpdate = lockService.lock(kodeId);
            Assert.assertEquals(kodeForUpdate.getKodeverdi(), "ABC", "Kodebeskrivelse før oppdatering");

            kodeForUpdate.setKodeverdi("DEF");
            UnitOfWorkTransfer updateTransfer = new UnitOfWorkTransfer(Collections.emptyList(), Collections.<BubbleObject>singletonList(kodeForUpdate), Collections.emptyList());
            updateService.saveTransfer(updateTransfer);

            List<SimpleLocalizedDbKodeId> postUpdateVersions = storeService.getVersions(kodeId, SnapshotVersion.START, SnapshotVersion.CURRENT);
            Assert.assertEquals(postUpdateVersions.size(), 2, "Antall historikkinnslag etter oppdatering");

            SimpleLocalizedDbKode kodeForDelete = lockService.lock(kodeId);
            Assert.assertEquals(kodeForDelete.getKodeverdi(), "DEF", "Kodebeskrivelse etter oppdatering");

            UnitOfWorkTransfer deleteTransfer = new UnitOfWorkTransfer(Collections.emptyList(), Collections.emptyList(), Collections.<BubbleObject>singletonList(kodeForDelete));
            updateService.saveTransfer(deleteTransfer);

            StoreTestKodelisteLong kodeliste2 = storeService.getObject(SimpleLocalizedDbKodeId.KODELISTE_ID);
            Assert.assertEquals(kodeliste2.getKoderIds(), Collections.emptyList());

            List<SimpleLocalizedDbKodeId> postDeleteVersions = storeService.getVersions(kodeId, SnapshotVersion.START, SnapshotVersion.CURRENT);
            Assert.assertEquals(postDeleteVersions.size(), 2, "Antall historikkinnslag etter sletting");
        } finally {
            cleanUp(kodeId);
        }
    }

    private SimpleLocalizedDbKodeId prepareId() {
        StoreTestMockupFacade emptyMockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
        return emptyMockupFacade.getIdService().getNextId(SimpleLocalizedDbKodeId.class);
    }

    private void cleanUp(final SimpleLocalizedDbKodeId kodeId) {
        RunOnServerWithTxRequiresNewService runOnServerService = injector.getInstance(RunOnServerWithTxRequiresNewService.class);
        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Connection connection;

            @Override
            public Object run() {
                try (PreparedStatement statement = connection.prepareStatement("delete from historiskdbkode_h where id=?")) {
                    statement.setLong(1, kodeId.getValue());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    throw new OperationalException(e);
                }

                return null;
            }
        });
    }
}
