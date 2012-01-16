package no.statkart.skif.util;

import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.JDBCConnectionFactory;
import no.statkart.skif.persistence5.jdbc.ConnectionFactoryUsingJDBC;
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
                throw new ImplementationException(e);
            }
        }
    }

    public static void close(ResultSet resultSet, Statement statement) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new ImplementationException(e);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new ImplementationException(e);
            }
        }
    }

    public static void setAutoCommit(Connection c, boolean b) {
        try {
            c.setAutoCommit(b);
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }

    }

    public static void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new ImplementationException(e);
            }
        }
    }

    public static JDBCConnectionFactory createJDBCConnectionFactory(Configuration configuration) {
        String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
        String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
        String sid = configuration.getString(SkifConfigConstants.DB_SID);
        String hostname = configuration.getString(SkifConfigConstants.DB_HOSTNAME);
        String port = configuration.getString(SkifConfigConstants.DB_PORT);
        String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);
        return new JDBCConnectionFactory(url, username, password);
    }

    public static ConnectionFactoryUsingJDBC createConnectionFactoryUsingJDBC(Configuration configuration, SnapshotVersion snapshotVersion, boolean setSnapshotOnSession) {
        String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
        String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
        String sid = configuration.getString(SkifConfigConstants.DB_SID);
        String hostname = configuration.getString(SkifConfigConstants.DB_HOSTNAME);
        String port = configuration.getString(SkifConfigConstants.DB_PORT);
        String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);
        return new ConnectionFactoryUsingJDBC(url, username, password, false, snapshotVersion, setSnapshotOnSession);
    }
}
