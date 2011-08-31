package no.statkart.skif.storetest.wsapi.service.storetest1;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface StoreTest1ServiceWSI extends ServiceWSI {
    public String put(@WebParam(name = "key")String key, @WebParam(name = "value")String value) throws ServiceException;
    public String get(@WebParam(name = "key")String key) throws ServiceException;
    public String remove(@WebParam(name = "key") String key) throws ServiceException;
    public void clear() throws ServiceException;
    public String putThatFails(@WebParam(name = "key")String key, @WebParam(name = "value")String value) throws ServiceException;
    public String putViaJDBCConnection(@WebParam(name = "key")String key, @WebParam(name = "value")String value) throws ServiceException;
}
