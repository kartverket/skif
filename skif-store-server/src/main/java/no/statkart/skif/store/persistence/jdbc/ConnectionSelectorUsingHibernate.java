package no.statkart.skif.store.persistence.jdbc;

import com.google.inject.Inject;
import no.statkart.skif.persistence.jdbc.ConnectionSelector;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.SessionSelector;

import java.sql.Connection;

/**
 * Klasse for å hente ut en Hibernate session og låse denne til å bruke en gitt snapshotversion. Når man er
 * ferdig med å bruke sessionen må den frigis slik at sessionen senere kan gjenbrukes for en annen snapshotversion.
 * Dette skjer automatisk når man skifter snapshotversion via selectoren og når selectoren lukkes.
 * <p>
 * <strong>Eksempel på bruk</strong>
 * <pre>
 *     class SessionSelectorUsage {
 *         @Inject
 *         Provider<SessionSelector> sessionSelectorProvider;
 *
 *         public void someMethod() {
 *             SessionSelector sessionSelector = sessionSelectorProvider.get();
 *             try {
 *                 Session session = sessionSelector.get(SnapshotVersion.OLD);
 *                 // Bruk session for OLD
 *             } finnaly {
 *                 if (sessionSelector!=null) sessionSelector.close();
 *             }
 *         }
 *     }
 * </pre>
 *
 * @author Henrik Fredholm
 */
public class ConnectionSelectorUsingHibernate implements ConnectionSelector {
    private SessionSelector sessionSelector;

    @Inject
    public ConnectionSelectorUsingHibernate(PersistenceSessionManager persistenceSessionManager) {
        this.sessionSelector = new SessionSelector(persistenceSessionManager);
    }

    @Override
    public Connection get(SnapshotVersion snapshotVersion) {
        return sessionSelector.get(snapshotVersion).connection();
    }


     @Override
     public void close() {
        sessionSelector.close();
    }
}
