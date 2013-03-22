package no.statkart.skif.storetest2.service.locker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.config.Configuration;

import java.sql.Connection;

/**
 * Knytter implementasjonen opp til service-interfacet.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class DBLockerServiceImpl extends no.statkart.skif.service.locker.DBLockerServiceImpl implements DBLockerService {
    @Inject
    public DBLockerServiceImpl(Provider<Connection> connectionProvider, Configuration configuration) {
        super(connectionProvider, configuration);
    }
}
