package no.statkart.skif.storetest.wsapi.service.test;

import jakarta.jws.WebParam;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.MockupSnapshotMap;
import no.statkart.skif.storetest.wsapi.domain.MockupTransfer;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.TestNumber;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

/**
 * @author Roar Ingebrigtsen
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public interface TestdataServiceWSI extends ServiceWSI {

    /**
     * @since 2.1
     */
    TestNumber getNextTestNumber(@WebParam(name = "context") StoreTestContext context) throws ServiceException;

    /**
     * @since 2.1
     */
    TestNumber getTestNumber0(@WebParam(name = "context") StoreTestContext context) throws ServiceException;

    /**
     * @since 2.1
     */
    void saveAll(@WebParam(name = "snapshotTransfers") MockupSnapshotMap snapshotTransfers, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    /**
     * @since 2.1
     */
    void saveSnapshotTransfer(@WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "mockupTransfer") MockupTransfer mockupTransfer, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    void deleteObject(@WebParam(name = "id") long id, @WebParam(name = "tableName") String tableName, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    boolean objectExists(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

}
