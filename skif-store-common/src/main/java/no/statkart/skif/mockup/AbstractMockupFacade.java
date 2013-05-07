package no.statkart.skif.mockup;

import com.google.inject.Inject;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Baseklasse for mockupfacader. Inneholder store og grunnleggende metoder for å hente ut transfer.
 * SKIF-applikasjoner lager sin egen implementasjon som definerer opp mockupfactories.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public abstract class AbstractMockupFacade {
    @Inject
    private MockupStore store;

    @Inject
    private TestNumber testNumber;

    @Inject
    private IdService idService;

    public MockupStore getStore() {
        return store;
    }

    public TestNumber getTestNumber() {
        return testNumber;
    }

    /**
     * @return En {@link IdService} som brukes for å generere test-sett spesifike id-er.
     */
    public IdService getIdService() {
        return idService;
    }

    public abstract List<? extends AbstractMockupFactory> getAllMockupFactories();

    public void createAllMockups() {
        for (AbstractMockupFactory mockupFactory : getAllMockupFactories()) {
            mockupFactory.createAllMockups();
        }
    }

    public MockupTransfer getTransfer() {
        return getTransfer(SnapshotVersion.CURRENT);
    }

    public MockupTransfer getTransfer(SnapshotVersion snapshotVersion) {
        return store.getTransfer(snapshotVersion);
    }

    public MockupTransfer getTransferForIds(Set<? extends BubbleId> ids) {
        return getTransferForIds(ids, SnapshotVersion.CURRENT);
    }

    public MockupTransfer getTransferForIds(Set<? extends BubbleId> ids, SnapshotVersion snapshotVersion) {
        return store.getTransferForIds(ids, snapshotVersion);
    }

    public SortedMap<SnapshotVersion, MockupTransfer> getAllTransfers() {
        return getTransfersBefore(SnapshotVersion.CURRENT);
    }

    /**
     * @since 2.3.0
     */
    public SortedMap<SnapshotVersion, MockupTransfer> getAllTransfersForIds(Set<? extends BubbleId> ids) {
        return getAllTransfersForIds(ids, SnapshotVersion.CURRENT);
    }

    /**
     * @since 2.3.0
     */
    public SortedMap<SnapshotVersion, MockupTransfer> getAllTransfersForIds(Set<? extends BubbleId> ids, SnapshotVersion beforeSnapshotVersion) {
        return store.getAllTransfersForIds(ids, beforeSnapshotVersion);
    }

    public SortedMap<SnapshotVersion, MockupTransfer> getTransfersBefore(SnapshotVersion beforeSnapshotVersion) {
        return store.getTransfersBefore(beforeSnapshotVersion);
    }
}
