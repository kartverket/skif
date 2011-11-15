package no.statkart.skif.storetest.service.locker;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.sql.Connection;

/**
 * Knytter implementasjonen opp til service-interfacet.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class DBLockerInTransactionServiceImpl extends no.statkart.skif.service.locker.DBLockerInTransactionServiceImpl implements DBLockerInTransactionService {
    @Inject
    public DBLockerInTransactionServiceImpl(Provider<Connection> connectionProvider) {
        super(connectionProvider);
    }
}
