package no.statkart.skif.storetest.service.test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.mockup.TestNumberFactory;
import no.statkart.skif.service.sequence.SequenceBlockAllocatorService;
import no.statkart.skif.store.Store;

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
    public TestdataServiceImpl(SequenceBlockAllocatorService sequenceBlockAllocatorService, Provider<Connection> connectionProvider, Store store, no.statkart.skif.service.test.TestdataService testdataService, TestNumberFactory testNumberFactory) {
        super(sequenceBlockAllocatorService, connectionProvider, store, testdataService, testNumberFactory);
    }

    @Override
    public void deleteObject(long id, String tableName) {
        Connection connection = connectionProvider.get();
        try (PreparedStatement statement = connection.prepareStatement("delete from " + tableName + " where id=?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OperationalException("Kunne ikke slette objekt", e);
        }
    }
}
