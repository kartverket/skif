package no.statkart.skif.persistence;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import no.statkart.skif.exception.ImplementationException;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Guice provider for å få tak i en Connection. Hver provider må opprettes med en key som angir
 * hvilken connection instans som er ønsket. Klassen bruker en ConnectionManager som hentes ut via
 * en provider slik at ConnectionManager kan ha ServiceRequestScope.
 * @author Henrik Fredholm
 * @since 2.0
 */
@Singleton
public class ConnectionProvider implements Provider<Connection> {
    private final Object key;

    // Denne forventes å ha ServiceRequestScope
    private Provider<ConnectionManager> connectionManagerProvider;

    public ConnectionProvider(Object key) {
        this.key = key;
    }

    @Inject
    public void setConnectionManager(Provider<ConnectionManager> connectionManagerProvider) {
        this.connectionManagerProvider = connectionManagerProvider;
    }

    @Override
    public Connection get() {
        try {
            return connectionManagerProvider.get().getConnection(key);
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }
}
