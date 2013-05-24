package no.statkart.skif.storetest2.kode;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.storetest2.domain.kode.AKode;
import no.statkart.skif.storetest2.domain.kode.AKodeId;
import no.statkart.skif.storetest2.domain.kodeliste.StoreTest2KodelisteLong;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2ServerTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Tester enkle kode-ting.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
@Test
public class KodeTest extends StoreTest2ServerTestCase {
    @Inject
    private Store store;

    public void hentAKoder() {
        StoreTest2KodelisteLong kodeliste = store.get(AKodeId.KODELISTE_ID);
        List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
        Assert.assertEquals(kodeIds.size(), 2, "Antall AKoder");

        List<Kode> koder = store.get(kodeIds);
        for (Kode kode : koder) {
            Assert.assertEquals(kode.getClass(), AKode.class, "Kodeklasse");
        }

        AKode y = store.get(new AKodeId(2L, SnapshotVersion.CURRENT));
        Assert.assertEquals(y.getBeskrivelse(), "Andre enkle meningsløse kode");
    }

    public void hentAKoderOld() {
        StoreTest2KodelisteLong kodeliste = store.get(AKodeId.KODELISTE_ID);
        List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
        Assert.assertEquals(kodeIds.size(), 2, "Antall AKoder");

        for (KodeId<?> kodeId : kodeIds) {
            Kode kode = (Kode) store.get(kodeId.asSnapshotVersionOld());
            Assert.assertEquals(kode.getClass(), AKode.class, "Kodeklasse");
            Assert.assertEquals(kode.getId().getSnapshotVersion(), SnapshotVersion.OLD, "SnapshotVersion");
        }

        AKode y = store.get(new AKodeId(2L, SnapshotVersion.OLD));
        Assert.assertEquals(y.getBeskrivelse(), "Andre enkle meningsløse kode");
    }

    public void hentAKoderHistorisk() {
        StoreTest2KodelisteLong kodeliste = store.get(AKodeId.KODELISTE_ID);
        List<KodeId<?>> kodeIds = kodeliste.getKodeIds();
        Assert.assertEquals(kodeIds.size(), 2, "Antall AKoder");

        final SnapshotVersion snapshotVersion = SnapshotVersion.createInstance("2010-01-01 00:00:00.00");
        for (KodeId<?> kodeId : kodeIds) {
            Kode kode = (Kode) store.get(kodeId.asSnapshotVersion(snapshotVersion));
            Assert.assertEquals(kode.getClass(), AKode.class, "Kodeklasse");
            Assert.assertEquals(kode.getId().getSnapshotVersion(), snapshotVersion, "SnapshotVersion");
        }

        AKode y = store.get(new AKodeId(2L, snapshotVersion));
        Assert.assertEquals(y.getBeskrivelse(), "En annen enkel meningsløse kode");
    }
}
