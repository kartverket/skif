package no.statkart.skif.skiftest.wsapi.service.testex;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.exception.SimpleException;
import no.statkart.skif.skiftest.wsapi.exception.SimpleNonMappedException;
import no.statkart.skif.skiftest.wsapi.domain.StringList;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface TestExServiceWSI extends ServiceWSI {
    public String noTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;
    public String requiresTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;
    public String newTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;
    public String nonMappedCall(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;
    public String indirectNoTx(@WebParam(name="callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;
    public String indirectRequiresTx(@WebParam(name="callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;
    public String indirectNewTx(@WebParam(name="callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException;
    public String indirectNoEx(@WebParam(name="callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message);
}
