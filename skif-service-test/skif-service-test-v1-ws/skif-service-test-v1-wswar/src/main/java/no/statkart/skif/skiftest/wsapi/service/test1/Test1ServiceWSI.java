package no.statkart.skif.skiftest.wsapi.service.test1;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.domain.SkifTestContext;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;

import jakarta.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface Test1ServiceWSI extends ServiceWSI {

    String helloWorld(@WebParam(name = "message") String message, @WebParam(name = "skifTestContext") SkifTestContext skifTestContext) throws ServiceException;

    String helloVersion(@WebParam(name = "message") String message, @WebParam(name = "skifTestContext") SkifTestContext skifTestContext) throws ServiceException;

}
