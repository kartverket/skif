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
    <I extends KodelisteId<?>> KodelisteTransfer<I> getKodelister(SnapshotVersion snapshotVersion);
    <I extends KodelisteId<?>> KodelisteTransfer<I> getKodeliste(String kodeIdClassName, SnapshotVersion snapshotVersion);   // TODO: Vurdere behov for å legge kodeIdClassName i en egen klasse for å forenlke mappingen av kodeIdKlasse fra ext api til intern api
}
