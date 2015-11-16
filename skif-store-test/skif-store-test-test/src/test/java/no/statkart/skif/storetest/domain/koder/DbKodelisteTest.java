package no.statkart.skif.storetest.domain.koder;

import com.google.inject.Inject;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.store.localization.LocalizedString;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.JDBCHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

/**
 * Tester oppdatering av databasekodelister.
 * <p>
 * Testen er på ingen måte representativ for faktisk bruk, da man aldri vil opprette eller slette kodelister på denne
 * måten. Oppdateringsdelen er muligens relevant.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Test(groups = "singlevm-required")
public class DbKodelisteTest extends StoreTestTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private StoreService storeService;

    @Inject
    private StoreUpdateService updateService;

    public void testInsertUpdateRemoveDbKodeliste() {
        StoreTestKodelisteLongId<?> kodelisteId = prepareId();

        Locale norsk = new Locale("no", "NO");

        LocalizedString localizedNavn = new LocalizedString();
        localizedNavn.setText(norsk, "Testkodeliste");

        try {
            StoreTestKodelisteLong kodelisteForInsert = new StoreTestKodelisteLong();
            kodelisteForInsert.setId(kodelisteId);
            kodelisteForInsert.setKodeTypeNavn("Testkodeliste");
            kodelisteForInsert.setKodeIdClass(SimpleLocalizedDbKodeId.class); // Misbruker en kodeid
            kodelisteForInsert.setNavn(localizedNavn);
            UnitOfWorkTransfer insertTransfer = new UnitOfWorkTransfer(Arrays.<BubbleObject>asList(kodelisteForInsert), Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList());
            updateService.saveTransfer(insertTransfer);

            // Det finnes ikke historikk på kodelister
//            List<? extends StoreTestKodelisteLongId<?>> postInsertVersions = storeService.getVersions(kodelisteId, SnapshotVersion.START, SnapshotVersion.CURRENT);
//            Assert.assertEquals(postInsertVersions.size(), 1, "Antall historikkinnslag etter opprettelse");

            StoreTestKodelisteLong kodelisteForUpdate = storeService.lock(kodelisteId);
            Assert.assertEquals(kodelisteForUpdate.getKodeTypeNavn(), "Testkodeliste", "Kodelistetype før oppdatering");
            Assert.assertEquals(kodelisteForUpdate.getNavn().getText(norsk), "Testkodeliste", "Kodelistenavn før oppdatering");

            LocalizedString navnForUpdate = kodelisteForUpdate.getNavn();
            navnForUpdate.setText(norsk, "Kodeliste for test");
            kodelisteForUpdate.setNavn(navnForUpdate);
            UnitOfWorkTransfer updateTransfer = new UnitOfWorkTransfer(Collections.<BubbleObject>emptyList(), Arrays.<BubbleObject>asList(kodelisteForUpdate), Collections.<BubbleObject>emptyList());
            updateService.saveTransfer(updateTransfer);

            // Det finnes ikke historikk på kodelister
//            List<? extends StoreTestKodelisteLongId<?>> postUpdateVersions = storeService.getVersions(kodelisteId, SnapshotVersion.START, SnapshotVersion.CURRENT);
//            Assert.assertEquals(postUpdateVersions.size(), 2, "Antall historikkinnslag etter oppdatering");

            StoreTestKodelisteLong kodelisteForDelete = storeService.lock(kodelisteId);
            Assert.assertEquals(kodelisteForDelete.getKodeTypeNavn(), "Testkodeliste", "Kodelistetype etter oppdatering");
            Assert.assertEquals(kodelisteForDelete.getNavn().getText(norsk), "Kodeliste for test", "Kodelistenavn etter oppdatering");

            UnitOfWorkTransfer deleteTransfer = new UnitOfWorkTransfer(Collections.<BubbleObject>emptyList(), Collections.<BubbleObject>emptyList(), Arrays.<BubbleObject>asList(kodelisteForDelete));
            updateService.saveTransfer(deleteTransfer);

            // Det finnes ikke historikk på kodelister
//            List<? extends StoreTestKodelisteLongId<?>> postDeleteVersions = storeService.getVersions(kodelisteId, SnapshotVersion.START, SnapshotVersion.CURRENT);
//            Assert.assertEquals(postDeleteVersions.size(), 2, "Antall historikkinnslag etter sletting");
        } finally {
            cleanUp(kodelisteId);
        }
    }

    private StoreTestKodelisteLongId prepareId() {
        StoreTestMockupFacade emptyMockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
        return emptyMockupFacade.getIdService().getNextId(StoreTestKodelisteLongId.class);
    }

    private void cleanUp(final StoreTestKodelisteLongId<?> kodelisteId) {
        RunOnServerWithTxRequiresNewService runOnServerService = injector.getInstance(RunOnServerWithTxRequiresNewService.class);
        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Connection connection;

            @Override
            public Object run() {
                PreparedStatement statement = null;

                try {
                    statement = connection.prepareStatement("delete from kodelisteloc where id=?");
                    statement.setLong(1, kodelisteId.getValue());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    throw new OperationalException(e);
                } finally {
                    JDBCHelper.close(statement);

                    try {
                        statement = connection.prepareStatement("delete from kodeliste where id=?");
                        statement.setLong(1, kodelisteId.getValue());
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        throw new OperationalException(e);
                    } finally {
                        JDBCHelper.close(statement);
                        statement = null;
                    }
                }

                return null;
            }
        });
    }
}
