package no.statkart.skif.wsversioning.wsapi.v1.service;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.wsversioning.wsapi.v1.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v1.domain.GateIdList;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceException;

/**
 * WebServiceInterface for {@link no.statkart.skif.wsversioning.service.VegService}.
 *
 * @author Tor Egil R. Strand
 */
public interface GateServiceWSI extends ServiceWSI {

    GateIdList findAlleGater(WSVersioningContext context) throws ServiceException;

}
