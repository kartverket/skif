package no.statkart.skif.store2.persistence;

import no.statkart.skif.persistence.ConnectionManager;
import no.statkart.skif.store.SnapshotVersion;

import java.sql.SQLException;

/**
 * Interface for håndterer {@code StoreSession2} instanser, her under åpning, lukking, flushing, commit, rollback samt
 * håndtering at {@code SnapshotVersion}.
 * </p>
 * {@code StoreSessionManager} implementasjoner kan tilbyde en eller flere {@StoreSession2}s som kan brukes parallelt.
 * Mest typisk er å tilbyd to {@code StoreSession2} instanser, en for {@link SnapshotVersion#CURRENT} og en for
 * {@link SnapshotVersion#OLD} som bruker hver sin underliggende connection/session mot databasen.
 * </p>
 * For å begrense behovet for underliggende sessioner til to er api'et for bruk av {@code StoreSession2} instanser
 * med {@code SnapshotVersion} designet slik at man må frigi sessionen så snart man er ferdi med å bruke den og man må
 * kun bruke den session man siste har bedt om. Skal man bruke en tidligere session må dem man har bedt om etter på
 * frigis først. Dette sikre at sessionen man bruke alltid bruker rigtig SnapshotVersion mot databasen.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreSessionManager2 extends ConnectionManager {
    /**
     * Henter ut StoreSession2 med gitt {@code snapshotVersion} som kun kan være {@link SnapshotVersion#CURRENT} eller
     * {@link SnapshotVersion#OLD}.
     * @deprecated
     */
    StoreSession2 getStoreSession(SnapshotVersion snapshotVersion);

    /**
     * Starter et nytt SnapshotVersion scope. Default scope er {@link SnapshotVersion#CURRENT}.
     * @param snapshotVersion
     */
    void beginSnapshotScope(SnapshotVersion snapshotVersion);

    /**
     * Avslutter inneværende SnapshotVersion scope.
     */
    void endSnapshotScope();


    /**
     * Henter ut en {@code StoreSession2} instans med {@code SnapshotVersion} satt til inneværende {@code SnapshotVersion} scope.
     */
    StoreSession2 acquireSnapshotStoreSessionUsingSnapshotScope();

    /**
     * Henter ut en {@code StoreSession2} med {@gitt SnapshotVersion} satt til gitt verdi.
     */
    StoreSession2 acquireSnapshotStoreSession(SnapshotVersion snapshotVersion);

    /**
     * Frigir {@code storeSession} og setter dens {@code SnapshotVersion} tilbake til forrige verdi sessionen hadde.
     */
    void releaseSnapshotStoreSession(StoreSession2 storeSession);
}
