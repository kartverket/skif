package no.statkart.skif.persistence.jdbc;

import no.statkart.skif.store.SnapshotVersion;

import java.sql.Connection;

/**
 * Klasse for å hente ut en JDBC connection og låse denne til å bruke en gitt snapshotversion. Når man er
 * ferdig med å bruke connectionen må den frigis slik at den senere kan gjenbrukes for en annen snapshotversion.
 * Dette skjer automatisk når man skifter snapshotversion via selectoren samt når selectoren lukkes.
 * <p>
 * <strong>Eksempel på bruk</strong>
 * <pre>
 *     class ConnectionSelectorUsage {
 *         @Inject
 *         Provider<ConnectionSelector> connectionSelectorProvider;
 *
 *         public void someMethod() {
 *             ConnectionSelector connectionSelector = connectionSelectorProvider.get();
 *             try {
 *                 Connection connection = connectionSelector.get(SnapshotVersion.OLD);
 *                 // Bruk connection for OLD
 *             } finnaly {
 *                 if (connectionSelector!=null) connectionSelector.close();
 *             }
 *         }
 *     }
 * </pre>
 *
 * @since 2.3
 * @author Henrik Fredholm
 *
 */
public interface ConnectionSelector {
    /**
     * Returnerer en connection som er låst til en gitt snapshotversion og som kun er gyldig sålenge det
     * ikke hentes ut en connection for en annen snapshotversion via selectoren.
     */
    Connection get(SnapshotVersion snapshotVersion);

    /**
     * Frigir inneværende connection dersom en slik har blit allokert og lukker selectoren slik at den ikke lengre kan brukes
     */
    void close();
}
