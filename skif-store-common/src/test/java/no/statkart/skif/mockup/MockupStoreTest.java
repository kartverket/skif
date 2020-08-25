package no.statkart.skif.mockup;

import no.statkart.skif.store.SnapshotVersion;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.SortedMap;

import static org.assertj.core.api.Assertions.assertThat;

public class MockupStoreTest {


    /**
     * Oppdaterer et objekt på et senere tidspunkt som utvider objektgraf.
     * Objekter som kobles til senere tas med i transfer.
     */
    @Test
    public void nyeRefererteObjekterReturneresForGetAllTransfersForIds() {
        final MockupStore mockupStore = new MockupStore(null, new TestNumber(0, 0), null, Collections.emptyList());
        final AdresseId adresseId = new AdresseId(1L);
        final KretsId kretsId = new KretsId(2L);
        final FlateId flateId = new FlateId(3L);

        final SnapshotVersion snapshotVersionStart = SnapshotVersion.createInstance("2000-01-01 00:00:00.0");
        mockupStore.setSnapshotVersion(snapshotVersionStart);
        mockupStore.insert(new Krets(kretsId, null));
        mockupStore.insert(new Adresse(adresseId, kretsId));

        final SnapshotVersion snapshotVersion2014 = SnapshotVersion.createInstance("2014-01-01 00:00:00.0");
        mockupStore.setSnapshotVersion(snapshotVersion2014);
        mockupStore.insert(new Flate(flateId));
        mockupStore.update(new Krets(kretsId, flateId));

        final SortedMap<SnapshotVersion, MockupTransfer> allTransfersForIds = mockupStore.getAllTransfersForIds(Collections.singleton(adresseId), SnapshotVersion.CURRENT);
        assertThat(allTransfersForIds.get(snapshotVersion2014).getInsertedObjects())
                .filteredOn(Flate.class::isInstance)
                .isNotEmpty();
    }
}
