package no.statkart.skif.storetest.service.test;

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
     *
     * @param id id-verdi
     * @param tableName tabellnavn
     */
    void deleteObject(long id, String tableName);

}
