package no.statkart.skif.skiftest.wsapi.service.id;

import no.statkart.skif.service.ws.ServiceWSI;

import javax.jws.WebParam;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface SequenceBlockAllocatorServiceWSI extends ServiceWSI {

    public long allocateSequenceBlock(@WebParam(name="sequenceName")String sequenceName,@WebParam(name="blockSize") int blockSize);
}
