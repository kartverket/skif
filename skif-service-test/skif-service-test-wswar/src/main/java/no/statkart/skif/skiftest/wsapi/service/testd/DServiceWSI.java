package no.statkart.skif.skiftest.wsapi.service.testd;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.domain.StringList;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import no.statkart.skif.skiftest.wsapi.exception.SimpleNonMappedException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface DServiceWSI extends ServiceWSI {
    public String noTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;
    public String noTxNested(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;
    public String requiresTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;
    public String newTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;
    public String nonMappedWSCall(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException, SimpleNonMappedException;
    public String nonMappedEJBCall(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;
    public String indirectNoTx(@WebParam(name="callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;
    public String indirectRequiresTx(@WebParam(name="callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;
    public String indirectNewTx(@WebParam(name="callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException;
    public String indirectNoEx(@WebParam(name="callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message);
}
