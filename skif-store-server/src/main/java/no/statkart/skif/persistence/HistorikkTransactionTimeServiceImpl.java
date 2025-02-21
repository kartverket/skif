package no.statkart.skif.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import no.statkart.skif.store.SnapshotVersionSessionHelper;

import java.sql.Connection;
import java.sql.Timestamp;

/**
 * Denne implementasjonen benytter {@link SnapshotVersionSessionHelper} til å hente ut transaksjonstidspunkt fra
 * en standard historikkimplementasjon i databasen.
 *
 * @since 2.8.0
 */
@Singleton
public class HistorikkTransactionTimeServiceImpl implements TransactionTimeService {
    private final Provider<Connection> connectionProvider;

    @Inject
    public HistorikkTransactionTimeServiceImpl(Provider<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Timestamp getTransactionTime() {
        return SnapshotVersionSessionHelper.getTransactionTime(connectionProvider.get());
    }
}
