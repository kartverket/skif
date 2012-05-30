package no.statkart.skif.storetest.service.test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.util.JDBCHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementasjon av {@link TestdataService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class TestdataServiceImpl extends no.statkart.skif.service.test.TestdataServiceImpl implements TestdataService {
    @Inject
    private Provider<Connection> connectionProvider;

    @Override
    public void deleteObject(long id, String tableName) {
        Connection connection = connectionProvider.get();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("delete from " + tableName + " where id=?");
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OperationalException("Kunne ikke slette objekt", e);
        } finally {
            JDBCHelper.close(statement);
        }
    }
}
