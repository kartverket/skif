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
}
