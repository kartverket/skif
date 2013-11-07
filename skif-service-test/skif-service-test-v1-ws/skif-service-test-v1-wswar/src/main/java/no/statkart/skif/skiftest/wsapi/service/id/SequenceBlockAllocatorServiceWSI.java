package no.statkart.skif.skiftest.wsapi.service.id;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface SequenceBlockAllocatorServiceWSI extends ServiceWSI {

    public long allocateSequenceBlock(@WebParam(name="sequenceName")String sequenceName,@WebParam(name="blockSize") int blockSize) throws ServiceException;
}
