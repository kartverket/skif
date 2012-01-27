package no.statkart.skif.storetest.service.test;

import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Interface for service for bruk i tester.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public interface TestService extends no.statkart.skif.service.test.TestNumberService {
    public void saveSnapshotTransfer(MockupTransfer transfer, SnapshotVersion snapshotVersion);
}
