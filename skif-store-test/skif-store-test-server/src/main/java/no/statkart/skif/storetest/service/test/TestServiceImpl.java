package no.statkart.skif.storetest.service.test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.service.id.SequenceBlockAllocatorService;
import no.statkart.skif.util.JDBCHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementasjon av {@link TestService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class TestServiceImpl implements TestService {
    @Inject
    private SequenceBlockAllocatorService sequenceBlockAllocatorService;

    @Inject
    private Provider<Connection> connectionProvider;

    @Inject
    private Provider<Store> storeProvider;

    @Override
    public int getNextTestNumber() {
        return (int) sequenceBlockAllocatorService.allocateSequenceBlock("TEST_NUMBER", 1);
    }

    @Override
    public void saveSnapshotTransfer(MockupTransfer transfer, SnapshotVersion snapshotVersion) {
        Store store = storeProvider.get();

        setTransactionSnapshot(snapshotVersion);

        for (BubbleObject bubbleObject : transfer.getInserts()) {
            store.insert(bubbleObject);
        }

        for (BubbleObject bubbleObject : transfer.getUpdates()) {
            store.lock(bubbleObject.getId());
            store.update(bubbleObject);
        }

        for (BubbleObject bubbleObject : transfer.getDeletes()) {
            store.lock(bubbleObject.getId());
            store.delete(bubbleObject);
        }
    }

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

    private void setTransactionSnapshot(SnapshotVersion transactionSnapshot) {
        if (!SnapshotVersion.CURRENT.equals(transactionSnapshot)) {
            Connection connection = connectionProvider.get();
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement("insert into SNAPSHOT_TRANS values (?)");
                statement.setTimestamp(1, transactionSnapshot.getTimestamp());
                int rader = statement.executeUpdate();
                if (rader != 1) {
                    throw new OperationalException("Kunne ikke sette transaksjonstidspunkt. Feil antall rader oppdatert: " + rader);
                }
            } catch (SQLException e) {
                throw new OperationalException("Kunne ikke sette transaksjonstidspunkt", e);
            } finally {
                JDBCHelper.close(statement);
            }
        }
    }
}
