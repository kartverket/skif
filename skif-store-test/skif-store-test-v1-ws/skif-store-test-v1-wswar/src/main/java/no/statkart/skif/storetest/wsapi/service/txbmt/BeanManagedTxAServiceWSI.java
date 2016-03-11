package no.statkart.skif.storetest.wsapi.service.txbmt;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface BeanManagedTxAServiceWSI extends ServiceWSI {

    void clear() throws ServiceException;

    String get(@WebParam(name = "key") String key) throws ServiceException;

    String put(@WebParam(name = "key") String key, @WebParam(name = "value") String value) throws ServiceException;

    void multiPut(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;

}
