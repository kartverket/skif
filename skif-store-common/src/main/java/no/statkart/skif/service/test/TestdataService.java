package no.statkart.skif.service.test;

import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.service.annotation.SuppressSnapshotVersionMapping;
import no.statkart.skif.store.BubbleId;
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
    TestNumber getTestNumber0();

    /**
     * Finner neste ledige testnummer.
     *
     * @return neste testnummer
     */
    TestNumber getNextTestNumber();

    /**
     * Lagrer alle snapshots for et mockup-sett.
     *
     * @param snapshotTransfers snapshot-ene i mockup-settet, sortert kronologisk
     */
    void saveAll(SortedMap<SnapshotVersion, MockupTransfer> snapshotTransfers);

    /**
     * Lagrer ett enkelt snapshot. Denne er kun ment for intern bruk og skal ikke kalles fra klienter.
     *
     * @param snapshotVersion tidspunkt for snapshot
     * @param mockupTransfer  transfer med alle objekter som endres på gitt tidspunkt
     */
    @SuppressSnapshotVersionMapping
    void saveSnapshotTransfer(SnapshotVersion snapshotVersion, MockupTransfer mockupTransfer);

    /**
     * Sjekker om objekt med gitt id finnes i databasen fra før. Klienter skal sende inn id til første objekt
     * i et readmockupsett før den eventuel sender over en hel transfer.
     *
     * @param id id til første objekt i mockupsett
     * @return {@code true} dersom (i alle fall deler av) mockupsettet allerede ligger i databasen
     * @since 2.3.0
     */
    boolean objectExists(BubbleId<?> id);

}
