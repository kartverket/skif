package no.statkart.skif.storetest.wsapi.service.test;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.MockupTransfer;
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;

import javax.jws.WebParam;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface TestServiceWSI extends ServiceWSI {
    public void saveSnapshotTransfer(@WebParam(name = "transfer")MockupTransfer transfer,@WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion,@WebParam(name = "context") StoreTestContext context);
    public void deleteObject(@WebParam(name = "id")long id,@WebParam(name = "tableName") String tableName, @WebParam(name = "context") StoreTestContext context);
}
