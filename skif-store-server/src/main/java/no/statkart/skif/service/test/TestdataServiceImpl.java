package no.statkart.skif.service.test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.mockup.TestNumberFactory;
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

    @Inject
    TestNumberFactory testNumberFactory;


    @Override
    public TestNumber getTestNumber0() {
        return testNumberFactory.create(0);
    }

    @Override
    public TestNumber getNextTestNumber() {
        return testNumberFactory.create((int)sequenceBlockAllocatorService.allocateSequenceBlock("TEST_NUMBER", 1));
    }

    @Override
    public void saveAll(SortedMap<SnapshotVersion, MockupTransfer> snapshotTransfers) {
        // Sjekk om testsettet allerede er skrevet til databasen ved å sjekke på om første id i transfer finnes
        SnapshotVersion firstSnapshot = snapshotTransfers.firstKey();
        MockupTransfer firstTransfer = snapshotTransfers.get(firstSnapshot);
        if (testsetExists(firstSnapshot, firstTransfer)) {
            if (!firstTransfer.getTestNumber().isNR_0()) {
                throw new ImplementationException("Testset already exists in database: " + firstTransfer.getTestNumber());
            }
        } else {
            try {
                for (Map.Entry<SnapshotVersion, MockupTransfer> entry : snapshotTransfers.entrySet()) {
                    testdataService.saveSnapshotTransfer(entry.getKey(), entry.getValue());
                }
            } catch (RuntimeException e) {
                if (firstTransfer.getTestNumber().isNR_0()) {
                    logger.error("Failed to persist complete read set! Other tests might fail until database is recreated!");
                }
                throw e;
            }
        }
    }

    /**
     * Sjekker om testset allerede finnes i databasen ved å sjekk om første id i transfer
     * finnes i databasen.
     */
    private boolean testsetExists(SnapshotVersion snapshot, MockupTransfer transfer) {
        BubbleObject bubbleObject = transfer.getInsertedObjects().iterator().next();
        boolean funnet;
        try {
            store.get(bubbleObject.getBubbleId().asSnapshotVersion(snapshot));
            funnet = true;
        } catch (ObjectNotFoundException e) {
            funnet = false;
        }
        return funnet;
    }

    @Override
    public void saveSnapshotTransfer(SnapshotVersion snapshotVersion, MockupTransfer transfer) {
        setTransactionSnapshot(snapshotVersion);
        try {
            store.beginUnitOfWork();
            store.lock(BubbleIds.asBaseIds(transfer.getUpdatedObjects()));
            store.lock(BubbleIds.asBaseIds(transfer.getDeletedObjects()));
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
                    throw new OperationalException("Could not set transaction timestamp. Wrong number of rows updated: " + rader);
                }
            } catch (SQLException e) {
                throw new OperationalException("Could not set transaction timestamp", e);
            } finally {
                JDBCHelper.close(statement);
            }
        }
    }

    @Override
    public boolean objectExists(BubbleId<?> id) {
        try {
            store.get(id);
            return true;
        } catch (ObjectNotFoundException e) {
            return false;
        }
    }
}
