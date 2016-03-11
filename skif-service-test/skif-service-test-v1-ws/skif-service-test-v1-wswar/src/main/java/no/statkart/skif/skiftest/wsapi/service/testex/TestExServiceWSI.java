package no.statkart.skif.skiftest.wsapi.service.testex;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.domain.StringList;
import no.statkart.skif.skiftest.wsapi.exception.SimpleException;
import no.statkart.skif.skiftest.wsapi.exception.SimpleNonMappedException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface TestExServiceWSI extends ServiceWSI {

    String noTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;

    String requiresTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;

    String newTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;

    String nonMappedCall(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;

    String indirectNoTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;

    String indirectRequiresTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;

    String indirectNewTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;

    String indirectNoEx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message);

}
