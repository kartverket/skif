package no.statkart.skif.mockup;

import com.google.inject.Inject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

import java.util.Arrays;
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

    public MockupStore getStore() {
        return store;
    }

    public TestNumber getTestNumber() {
        return testNumber;
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

    public SortedMap<SnapshotVersion, MockupTransfer> getTransfersBefore(SnapshotVersion beforeSnapshotVersion) {
        return store.getTransfersBefore(beforeSnapshotVersion);
    }
}
