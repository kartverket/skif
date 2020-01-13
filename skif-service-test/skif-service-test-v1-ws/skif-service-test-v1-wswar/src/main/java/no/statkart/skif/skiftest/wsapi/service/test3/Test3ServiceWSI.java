package no.statkart.skif.skiftest.wsapi.service.test3;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.domain.A;
import no.statkart.skif.skiftest.wsapi.domain.B;
import no.statkart.skif.skiftest.wsapi.domain.SkifTestContext;
import no.statkart.skif.skiftest.wsapi.exception.SimpleException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface Test3ServiceWSI extends ServiceWSI {

    A b2A(@WebParam(name = "b") B b, @WebParam(name = "skifTestContext") SkifTestContext skifTestContext);

    String testExceptionThrowing(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message, @WebParam(name = "skifTestContext") SkifTestContext skifTestContext) throws SimpleException;

}
