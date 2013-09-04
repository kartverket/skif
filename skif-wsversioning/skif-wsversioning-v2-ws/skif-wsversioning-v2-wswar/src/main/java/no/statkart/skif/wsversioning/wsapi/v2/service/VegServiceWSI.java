package no.statkart.skif.wsversioning.wsapi.v2.service;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.wsversioning.wsapi.v2.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v2.domain.VegIdList;
import no.statkart.skif.wsversioning.wsapi.v2.exception.ServiceException;

/**
 * WebServiceInterface for {@link no.statkart.skif.wsversioning.service.VegService}.
 *
 * @author Tor Egil R. Strand
 */
public interface VegServiceWSI extends ServiceWSI {
    public VegIdList findAlleVeger(WSVersioningContext context) throws ServiceException;
}
