package no.statkart.skif.persistence.jdbc;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.store.SnapshotVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection Manager som oppretter connections via factories. Denne implementasjon understøtter ikke endring av
 * SnapshotVersion på Connections.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ConnectionManagerUsingFactory implements ConnectionManager {
    private static Logger logger = LoggerFactory.getLogger(ConnectionManagerUsingFactory.class);
    private final ConnectionFactory[] factories;
    private final ConnectionForSnapshotVersion[] connections;
    private final boolean[] originalAutoCommits;
    private boolean isActive;

    public ConnectionManagerUsingFactory(ConnectionFactory... factories) {
        this.factories = factories;
        this.connections = new ConnectionForSnapshotVersion[factories.length];
        this.originalAutoCommits = new boolean[factories.length];

        for (int i = 0; i < factories.length; i++) {
            ConnectionFactory factory = factories[i];
            if (factory.isSnapshotChangable()) {
                throw new ImplementationException("ConnectionManager does not support ConnectionFactories where SnapshotVersion can be changed");
            }
        }
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive() {
        isActive=true;
    }

    protected void openConnection(int i,  SnapshotVersion snapshotVersion) {
        try {
            Connection delegate = factories[i].createConnection();
            connections[i] = new ConnectionProxyUsingJDBC(delegate, snapshotVersion, factories[i]).getProxy();
            originalAutoCommits[i] = connections[i].getAutoCommit();
            if (originalAutoCommits[i]) {
                connections[i].setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new OperationalException("Could not open and initialize JDBC connection", e);
        }
    }

    protected void closeConnection(int i) {
        try {
            logger.debug("Close Connection");
            if (originalAutoCommits[i]) {
                connections[i].setAutoCommit(false);
            }
            connections[i].close();
            connections[i] = null;
        } catch (SQLException e) {
            throw new OperationalException("Could not close JDBC connection", e);
        }
    }

    @Override
    public ConnectionForSnapshotVersion getForSnapshotVersion(SnapshotVersion snapshotVersion) {
        for (int i = 0; i < factories.length; i++) {
            ConnectionFactory factory = factories[i];
            if (factory.accepts(snapshotVersion)) {
                if (connections[i] == null) {
                    openConnection(i, snapshotVersion);
                }
                return connections[i];
            }
        }
        throw new ImplementationException("Found no ConnectionFactory for " + snapshotVersion);
    }

    @Override
    public void close() {
        isActive=false;
        for (int i = 0; i < connections.length; i++) {
            Connection connection = connections[i];
            if (connection != null) {
                closeConnection(i);
            }
        }
    }

    @Override
    public void beginTransaction() {
        // Ikke nødvendig å gjøre noe her
    }

    @Override
    public void flush() {
        // Ikke nødvendig å gjøre noe her
    }

    @Override
    public void commit() {
        if (connections[0] != null) {
            try {
                connections[0].commit();
            } catch (SQLException e) {
                throw new OperationalException("Could not commit JDBC transaction", e);
            }
        }
    }

    @Override
    public void rollback() {
        if (connections[0] != null) {
            try {
                connections[0].rollback();
            } catch (SQLException e) {
                throw new OperationalException("Could not roll back JDBC exception", e);
            }
        }
    }

}