package no.statkart.skif.storetest.wsapi.service.storetest1;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreTest1ServiceWSI extends ServiceWSI {

    String put(@WebParam(name = "key") String key, @WebParam(name = "value") String value) throws ServiceException;

    String get(@WebParam(name = "key") String key) throws ServiceException;

    String remove(@WebParam(name = "key") String key) throws ServiceException;

    void clear() throws ServiceException;

    String putThatFails(@WebParam(name = "key") String key, @WebParam(name = "value") String value) throws ServiceException;

    String putViaJDBCConnection(@WebParam(name = "key") String key, @WebParam(name = "value") String value) throws ServiceException;

}
