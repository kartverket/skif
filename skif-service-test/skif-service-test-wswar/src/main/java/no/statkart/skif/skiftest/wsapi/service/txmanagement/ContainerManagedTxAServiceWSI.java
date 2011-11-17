package no.statkart.skif.skiftest.wsapi.service.txmanagement;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ContainerManagedTxAServiceWSI extends ServiceWSI {
    public void clear() throws ServiceException;
    public String get(@WebParam(name = "key") String key) throws ServiceException;
    public String put(@WebParam(name = "key") String key, @WebParam(name = "value") String value) throws ServiceException;
    public void multiPut(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
}
