package no.statkart.skif.storetest.service.test;

import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Interface for service for bruk i tester.
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface TestdataService extends no.statkart.skif.service.test.TestdataService {

    /**
     * TODO: Ta bort
     * Hjelpe tjeneste for skif-tester som brukes til å slette data i tester som ikke bruker unike testdatasett
     * @param id
     * @param tableName
     */
    public void deleteObject(long id, String tableName);
}
