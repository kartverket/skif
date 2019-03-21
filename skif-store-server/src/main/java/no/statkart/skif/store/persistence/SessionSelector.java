package no.statkart.skif.store.persistence;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import org.hibernate.internal.SessionImpl;

import java.util.Objects;

/**
 * Klasse for å hente ut en Hibernate session og låse denne til å bruke en gitt snapshotversion. Når man er
 * ferdig med å bruke sessionen må den frigis slik at sessionen senere kan gjenbrukes for en annen snapshotversion.
 * Dette skjer automatisk når man skifter snapshotversion via selectoren samt når selectoren lukkes.
 * <p>
 * <strong>Eksempel på bruk</strong>
 * <pre>
 *     class SessionSelectorUsage {
 *         &#064;Inject
 *         Provider<SessionSelector> sessionSelectorProvider;
 *
 *         public void someMethod() {
 *             try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
 *                 Session session = sessionSelector.get(SnapshotVersion.OLD);
 *                 // Bruk session for OLD
 *             }
 *         }
 *     }
 * </pre>
 *
 * @author Henrik Fredholm
 */
public class SessionSelector implements AutoCloseable {
    private PersistenceSessionManager persistenceSessionManager;
    private HibernatePersistenceSessionMaster implementation;
    private SessionImpl session;

    @Inject
    public SessionSelector(PersistenceSessionManager persistenceSessionManager) {
        this.persistenceSessionManager = persistenceSessionManager;
    }

    /**
     * Returnerer en session som er låst til en gitt snapshotversion. Sessionen er kun gyldig sålenge det ikke hentes
     * ut en session for en annen snapshotversion via selectoren.
     */
    public SessionImpl get(SnapshotVersion snapshotVersion) {
        if (implementation == null) {
            reserveForSnapshot(snapshotVersion);
        } else if (!snapshotVersion.equals(this.implementation.getSnapshot())) {
            release();
            reserveForSnapshot(snapshotVersion);
        }
        return session;
    }


    /**
     * Frigir inneværende session dersom en slik har blit allokert og lukker selectoren slik at den ikke lengre kan brukes
     */
    @Override
    public void close() {
        release();
        persistenceSessionManager = null;
    }

    private void reserveForSnapshot(SnapshotVersion snapshotVersion) {
        Objects.requireNonNull(persistenceSessionManager, () -> "SessionSelector is closed: " + this);
        implementation = persistenceSessionManager.getForSnapshotVersion(snapshotVersion).getImplementation(HibernatePersistenceSessionMaster.class);
        session = implementation.reserveSession();
    }

    /**
     * Frigir inneværende session dersom en slik har blit allokert. Selectoren kan forsatt brukes.
     */
    private void release() {
        if (implementation != null) {
            implementation.releaseSession();
            implementation = null;
            session = null;
        }
    }
}
