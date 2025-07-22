package no.statkart.skif.skiftest.wsapi.service.testd;

import jakarta.jws.WebParam;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.domain.StringList;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import no.statkart.skif.skiftest.wsapi.exception.SimpleNonMappedException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface DServiceWSI extends ServiceWSI {

    String noTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;

    String noTxNested(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;

    String requiresTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;

    String newTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;

    String nonMappedWSCall(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException, SimpleNonMappedException;

    String nonMappedEJBCall(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;

    String indirectNoTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;

    String indirectRequiresTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;

    String indirectNewTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;

    String indirectNoEx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message);

}
