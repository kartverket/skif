package no.statkart.skif.service.test;

import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.store.SnapshotVersion;

import java.util.SortedMap;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface TestdataService {
    /**
     * Returnerer testnummer 0
     *
     * @return neste testnummer
     */
    public TestNumber getTestNumber0();

    /**
     * Finner neste ledige testnummer.
     *
     * @return neste testnummer
     */
    public TestNumber getNextTestNumber();

    /**
     * Lagrer alle snapshots for et mockup-sett.
     *
     * @param snapshotTransfers snapshot-ene i mockup-settet, sortert kronologisk
     */
    public void saveAll(SortedMap<SnapshotVersion, MockupTransfer> snapshotTransfers);

    /**
     * Lagrer ett enkelt snapshot. Denne er kun ment for intern bruk og skal ikke kalles fra klienter.
     *
     * @param snapshotVersion tidspunkt for snapshot
     * @param mockupTransfer transfer med alle objekter som endres på gitt tidspunkt
     */
    public void saveSnapshotTransfer(SnapshotVersion snapshotVersion, MockupTransfer mockupTransfer);
}
