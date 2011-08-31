package no.statkart.skif.store.persistence;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import no.statkart.skif.store.ReplicaVersion;

import javax.inject.Inject;

/**
 * Guice provider for å få tak i en StoreSession. Hver provider må opprettes med en key som angir
 * hvilken StoreSession instans som er ønsket. Klassen bruker en StoreSessionManager som hentes ut via
 * en provider slik at StoreSessionManager kan ha ServiceRequestScope.
 * @author Henrik Fredholm
 * @since 2.0
 */
@Singleton
public class StoreSessionProvider implements Provider<StoreSession> {
    private final ReplicaVersion replicaVersion;

    // Denne forventes å ha ServiceRequestScope
    private Provider<StoreSessionManager> storeSessionManagerProvider;

    public StoreSessionProvider(ReplicaVersion replicaVersion) {
        this.replicaVersion = replicaVersion;
    }

    @Inject
    public void setStoreSesisonManager(Provider<StoreSessionManager> storeSessionManagerProvider) {
        this.storeSessionManagerProvider = storeSessionManagerProvider;
    }

    @Override
    public StoreSession get() {
        return null; //storeSessionManagerProvider.get().getSession(replicaVersion);
    }
}
