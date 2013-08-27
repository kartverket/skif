package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.hibernate.Session;

import static org.testng.Assert.assertNotNull;

/**
 * Tester bruk av Oracle ARRAY i quiries via Hibernate user type og jdbc
 *
 * @author Henrik Fredholm
 */
public class SelectionSelectorTest extends StoreTestServerTestCase {
    @Inject
    Provider<SessionSelector> sessionSelectorProvider;

    public void testGetSessionForCurrentSnapshot() {
        SessionSelector sessionSelector= sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(SnapshotVersions.CURRENT);
            assertNotNull(session);
            // Bruk session ...

        } finally {
            if (sessionSelector!=null) sessionSelector.close();
        }
    }

    public void testGetSessionForOldSnapshot() {
        SessionSelector sessionSelector= sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(SnapshotVersions.OLD);
            assertNotNull(session);
            // Bruk session ...
        } finally {
            if (sessionSelector!=null) sessionSelector.close();
        }
    }

    public void testGetSessionForMultipleSnapshots() {
        testGetSessionForCurrentSnapshot();
        testGetSessionForOldSnapshot();
    }

    public void testSwithcingBetweenSnapshotsInSameTryBlock() {
        SessionSelector sessionSelector= sessionSelectorProvider.get();
        Session session;
        try {
             session= sessionSelector.get(SnapshotVersions.CURRENT);
             assertNotNull(session);
            // Bruk session ...

            session= sessionSelector.get(SnapshotVersions.OLD);
            assertNotNull(session);
            // Bruk session ...

            session= sessionSelector.get(SnapshotVersions.S1);
            assertNotNull(session);
            // Bruk session ...
            session= sessionSelector.get(SnapshotVersions.S2);
            assertNotNull(session);
            // Bruk session ...

        } finally {
            if (sessionSelector!=null) sessionSelector.close();
        }
    }

    public void testSwithcingBetweenSnapshotsInMultipleTryBlocks() {
        SessionSelector sessionSelector;
        Session session;

        sessionSelector= sessionSelectorProvider.get();
        try {
            session= sessionSelector.get(SnapshotVersions.CURRENT);
            assertNotNull(session);
        } finally {
            if (sessionSelector!=null) sessionSelector.close();
        }


        sessionSelector= sessionSelectorProvider.get();
        try {
            session= sessionSelector.get(SnapshotVersions.S1);
            assertNotNull(session);
        } finally {
            if (sessionSelector!=null) sessionSelector.close();
        }

        sessionSelector= sessionSelectorProvider.get();
        try {
            session= sessionSelector.get(SnapshotVersions.S2);
            assertNotNull(session);
        } finally {
            if (sessionSelector!=null) sessionSelector.close();
        }

        // Her er ingen sessioner låst.
        // TODO: Vise at det går fint an å navigere i objekt graf om laste nye objekter for flere snapshots samtidig.

    }
}

