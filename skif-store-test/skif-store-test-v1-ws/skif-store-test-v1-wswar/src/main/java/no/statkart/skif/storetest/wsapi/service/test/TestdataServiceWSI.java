package no.statkart.skif.storetest.wsapi.service.test;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Roar Ingebrigtsen
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public interface TestdataServiceWSI extends ServiceWSI {
    /** @since 2.1 */
    public TestNumber getNextTestNumber(@WebParam(name = "context") StoreTestContext context) throws ServiceException;

    /** @since 2.1 */
    public TestNumber getTestNumber0(@WebParam(name = "context") StoreTestContext context) throws ServiceException;

    /** @since 2.1 */
    public void saveAll(@WebParam(name = "snapshotTransfers") MockupSnapshotMap snapshotTransfers, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    /** @since 2.1 */
    public void saveSnapshotTransfer(@WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "mockupTransfer") MockupTransfer mockupTransfer, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    public void deleteObject(@WebParam(name = "id") long id, @WebParam(name = "tableName") String tableName, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    public boolean objectExists(@WebParam(name="id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context)throws ServiceException;
}
