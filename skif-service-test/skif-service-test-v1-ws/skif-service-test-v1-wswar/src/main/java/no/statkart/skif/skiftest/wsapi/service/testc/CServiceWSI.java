package no.statkart.skif.skiftest.wsapi.service.testc;

import jakarta.jws.WebParam;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.domain.StringList;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface CServiceWSI extends ServiceWSI {

    String m1(@WebParam(name = "callSpec") StringList callSpec) throws ServiceException;

    String m2(@WebParam(name = "callSpec") StringList callSpec) throws ServiceException;

    String m3(@WebParam(name = "callSpec") StringList callSpec)throws ServiceException;

}
