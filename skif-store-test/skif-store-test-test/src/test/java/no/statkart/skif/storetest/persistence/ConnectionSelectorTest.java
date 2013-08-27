package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.persistence.jdbc.ConnectionSelector;
import no.statkart.skif.store.persistence.jdbc.ConnectionSelectorUsingHibernate;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;

import java.sql.Connection;

import static org.testng.Assert.assertNotNull;

/**
 * Tester bruk av Oracle ARRAY i quiries via Hibernate user type og jdbc
 *
 * @author Henrik Fredholm
 */
public class ConnectionSelectorTest extends StoreTestServerTestCase {
    @Inject
    Provider<ConnectionSelectorUsingHibernate> connectionSelectorProvider;

    public void testGetConnectionForCurrentSnapshot() {
        ConnectionSelector connectionSelector= connectionSelectorProvider.get();
        try {
            Connection connection = connectionSelector.get(SnapshotVersions.CURRENT);
            assertNotNull(connection);
            // Bruk connection ...

        } finally {
            if (connectionSelector!=null) connectionSelector.close();
        }
    }

    public void testGetConnectionForOldSnapshot() {
        ConnectionSelector connectionSelector= connectionSelectorProvider.get();
        try {
            Connection connection = connectionSelector.get(SnapshotVersions.OLD);
            assertNotNull(connection);
            // Bruk connection ...

        } finally {
            if (connectionSelector!=null) connectionSelector.close();
        }
    }

    public void testGetSessionForMultipleSnapshots() {
        testGetConnectionForCurrentSnapshot();
        testGetConnectionForOldSnapshot();
    }

}

