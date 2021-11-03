package no.statkart.skif.util;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.jdbc.ConnectionSelector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Henrik Fredholm
 */
public class JDBCHelper {
    /**
     * @deprecated bruk heller try-with-resource
     */
    @Deprecated
    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new ImplementationException("Error closing statement", e);
            }
        }
    }

    /**
     * @deprecated bruk heller try-with-resource
     */
    @Deprecated
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

    /**
     * @deprecated bruk heller try-with-resource
     */
    @Deprecated
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

    /**
     * @deprecated bruk heller try-with-resource
     */
    @Deprecated
    public static void close(ConnectionSelector connectionSelector) {
        if (connectionSelector!=null) connectionSelector.close();
    }
}
