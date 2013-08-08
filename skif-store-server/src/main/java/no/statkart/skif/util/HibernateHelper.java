package no.statkart.skif.util;

import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.jdbc.ConnectionFactoryUsingJDBC;
import no.statkart.skif.persistence.jdbc.ConnectionSelector;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.SessionSelector;

import java.sql.*;

/**
 * @author Henrik Fredholm
 */
public class HibernateHelper {
    public static void close(Statement statement) {
        JDBCHelper.close(statement);
    }

    public static void close(PreparedStatement preparedStatement, SessionSelector sessionSelector) {
        try {
            close(preparedStatement);
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }
}
