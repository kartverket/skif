package no.statkart.skif.util;

import no.statkart.skif.exception.ImplementationException;

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
}
