package no.statkart.skif.service.test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.service.sequence.SequenceBlockAllocatorService;
import no.statkart.skif.store.*;
import no.statkart.skif.util.JDBCHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.SortedMap;

/**
 * Tjeneste for å legge inn testdata generert via mockup rammeverket i en database. For hver testsett som legges inn
 * undersøkes det om datasettet finnes fra før i databasen. Kun datasett for {@code TestNumber.NR_0} kan finnes fra før.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class TestdataServiceImpl implements TestdataService {
    protected Logger logger = LoggerFactory.getLogger(TestdataServiceImpl.class);

    @Inject
    private SequenceBlockAllocatorService sequenceBlockAllocatorService;

    @Inject
    private Provider<Connection> connectionProvider;

    @Inject
    private Store store;

    // Denne trengs for å få service kall til å gå via service rammeverket i stedet for direkte
    @Inject
    TestdataService testdataService;


    @Override
    public TestNumber getNextTestNumber() {
        return new TestNumber((int) sequenceBlockAllocatorService.allocateSequenceBlock("TEST_NUMBER", 1));
    }

    @Override
    public void saveAll(SortedMap<SnapshotVersion, MockupTransfer> snapshotTransfers) {
        // Sjekk om testsettet allerede er skrevet til databasen
        MockupTransfer firstTransfer = snapshotTransfers.values().iterator().next();
        boolean saveTestSet = !testsetExists(firstTransfer);
        if (saveTestSet) {
            for (Map.Entry<SnapshotVersion, MockupTransfer> entry : snapshotTransfers.entrySet()) {
                testdataService.saveSnapshotTransfer(entry.getKey(), entry.getValue());
            }
        }
    }

    private boolean testsetExists(MockupTransfer transfer) {
        BubbleObject bubbleObject = transfer.getInsertedObjects().iterator().next();
        boolean funnet;
        try {
            store.get(bubbleObject.getBubbleId());
            funnet = true;
        } catch (ObjectNotFoundException e) {
            funnet = false;
        }

        if (funnet) {
            if (!transfer.getTestNumber().equals(TestNumber.NR_0)) {
                throw new ImplementationException("Testsettet finnes allerede i databasen: " + transfer.getTestNumber());
            }
        }
        return funnet;
    }

    @Override
    public void saveSnapshotTransfer(SnapshotVersion snapshotVersion, MockupTransfer transfer) {
        final boolean saveTestSet = !(SnapshotVersion.CURRENT == snapshotVersion && testsetExists(transfer));
        if (saveTestSet) {
            saveSnapshotTransferInternal(snapshotVersion, transfer);
        }
    }

    private void saveSnapshotTransferInternal(SnapshotVersion snapshotVersion, MockupTransfer transfer) {
        setTransactionSnapshot(snapshotVersion);
        try {
            store.beginUnitOfWork();
            store.lock(BubbleIds.asIds(transfer.getUpdatedObjects()));
            store.lock(BubbleIds.asIds(transfer.getDeletedObjects()));
            store.registerTransfer(transfer);
            store.commitUnitOfWork();
        } catch (RuntimeException e) {
            store.abortUnitOfWork();
            throw e;

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
