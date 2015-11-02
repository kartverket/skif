package no.statkart.skif.util;

import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.jdbc.ConnectionFactoryUsingJDBC;
import no.statkart.skif.persistence.jdbc.ConnectionSelector;
import no.statkart.skif.store.SnapshotVersion;

import java.sql.*;

/**
 * @author Henrik Fredholm
 */
public class JDBCHelper {
    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new ImplementationException("Error closing statement", e);
            }
        }
    }

    @Deprecated // Unødvendig å angi resultSett sammen med Statement da ResultSett lukkes automatisk
    public static void close(ResultSet resultSet, Statement statement) {
        try {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new ImplementationException("Error closing result set", e);
                }
            }
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new ImplementationException("Error closing statement", e);
                }
            }
        }

    }

    public static void close(Statement statement, ConnectionSelector connectionSelector) {
        try {
            close(statement);
        } finally {
            close(connectionSelector);
        }
    }

    public static void setAutoCommit(Connection c, boolean b) {
        try {
            c.setAutoCommit(b);
        } catch (SQLException e) {
            throw new ImplementationException("Could not changing auto-commit setting", e);
        }

    }

    public static void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new ImplementationException("Error during rollback", e);
            }
        }
    }

    public static ConnectionFactoryUsingJDBC createConnectionFactoryUsingJDBC(Configuration configuration, SnapshotVersion snapshotVersion, boolean setSnapshotOnSession) {
        String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
        String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
        String service = configuration.getString(SkifConfigConstants.DB_SERVICE);
        String hostname = configuration.getString(SkifConfigConstants.DB_HOSTNAME);
        String port = configuration.getString(SkifConfigConstants.DB_PORT);
        String url = String.format("jdbc:oracle:thin:@//%s:%s/%s", hostname, port, service);
        return new ConnectionFactoryUsingJDBC(url, username, password, false, snapshotVersion, setSnapshotOnSession);
    }

    public static void close(ConnectionSelector connectionSelector) {
        if (connectionSelector!=null) connectionSelector.close();
    }
}
