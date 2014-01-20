package no.statkart.skif.storetest.domain.koder;

import com.google.inject.Inject;
import no.statkart.skif.exception.FinderException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tester at enumkoder virker med historikk.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Test
public class HistorikkEnumKodeTest extends StoreTestTestCase {
    @Inject
    private Store store;

    public void kodelisteUtenHistorikk() {
        StoreTestKodelisteLong kodeliste = store.get(SimpleEnumKodeId.KODELISTE_ID);
        List<KodeId<?>> kodeIds = kodeliste.getKoderIds();

        Assert.assertEquals(kodeIds, Arrays.asList(SimpleEnumKodeId.IkkeOppgittId, SimpleEnumKodeId.KodeAId, SimpleEnumKodeId.KodeBId));
    }

    public void kodelisteMedHistorikk() {
        SnapshotVersion past = SnapshotVersion.createInstance("2012-09-10 00:00:00.0");

        StoreTestKodelisteLong kodelisteCurrent = store.get(HistorikkEnumKodeId.KODELISTE_ID.asSnapshotVersion(SnapshotVersion.CURRENT));
        List<KodeId<?>> kodeIdsCurrent = kodelisteCurrent.getKoderIds();
        Assert.assertEquals(kodeIdsCurrent, Arrays.asList(HistorikkEnumKodeId.Kode1Id, HistorikkEnumKodeId.Kode3Id), "Current");

        StoreTestKodelisteLong kodelistePast = store.get(HistorikkEnumKodeId.KODELISTE_ID.asSnapshotVersion(past));
        List<KodeId<?>> kodeIdsPast = kodelistePast.getKoderIds();
        Assert.assertEquals(kodeIdsPast, Arrays.asList(HistorikkEnumKodeId.Kode1Id.asSnapshotVersion(past), HistorikkEnumKodeId.Kode2Id.asSnapshotVersion(past), HistorikkEnumKodeId.Kode3Id.asSnapshotVersion(past)), "Past");

        StoreTestKodelisteLong kodelisteVeryPast = store.get(HistorikkEnumKodeId.KODELISTE_ID.asSnapshotVersion(SnapshotVersion.START));
        List<KodeId<?>> kodeIdsVeryPast = kodelisteVeryPast.getKoderIds();
        Assert.assertEquals(kodeIdsVeryPast, Arrays.asList(HistorikkEnumKodeId.Kode1Id.asSnapshotVersion(SnapshotVersion.START), HistorikkEnumKodeId.Kode2Id.asSnapshotVersion(SnapshotVersion.START)), "Very past");
    }

    public void getHistoriskKode() {
        try {
            store.get(HistorikkEnumKodeId.Kode2Id);
            Assert.fail("Skulle fått exception");
        } catch (FinderException e) { // Egentlig ObjectNotFoundException
            // OK
        }

        HistorikkEnumKodeId kodeId = (HistorikkEnumKodeId) HistorikkEnumKodeId.Kode2Id.asSnapshotVersion(SnapshotVersion.createInstance("2012-09-10 00:00:00.0"));
        HistorikkEnumKode kode = store.get(kodeId);
        Assert.assertEquals(kode.getSluttdato(), SnapshotVersion.CURRENT.getTimestamp(), "Sluttdato er current før den er slettet");
    }
}
