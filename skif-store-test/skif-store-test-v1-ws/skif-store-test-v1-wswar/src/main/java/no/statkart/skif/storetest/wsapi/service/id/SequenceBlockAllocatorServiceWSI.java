package no.statkart.skif.storetest.wsapi.service.id;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;

import javax.jws.WebParam;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface SequenceBlockAllocatorServiceWSI extends ServiceWSI {

    public long allocateSequenceBlock(@WebParam(name="sequenceName")String sequenceName,@WebParam(name="blockSize") int blockSize, @WebParam(name = "context") StoreTestContext context);
}
