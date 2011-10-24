package no.statkart.skif.util;

import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.JDBCConnectionFactory;

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
        String username = configuration.getString(ConfigurationConstants.DB_USERNAME);
        String password = configuration.getString(ConfigurationConstants.DB_PASSWORD);
        String sid = configuration.getString(ConfigurationConstants.DB_SID);
        String hostname = configuration.getString(ConfigurationConstants.DB_HOSTNAME);
        String port = configuration.getString(ConfigurationConstants.DB_PORT);
        String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);
        return new JDBCConnectionFactory(url, username, password);
    }

}
