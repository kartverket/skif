package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Service for å hent alle kodelister
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface KodelisteService {
    KodelisteTransfer<? extends KodelisteId<?>> getKodelister(SnapshotVersion snapshotVersion);

    /**
     * @deprecated Navnet på KodeId-klassen er en dårlig idé å eksponere gjennom API-et. Metoden har heller aldri blitt implementert.
     */
    KodelisteTransfer<? extends KodelisteId<?>> getKodeliste(String kodeIdClassName, SnapshotVersion snapshotVersion);
}
