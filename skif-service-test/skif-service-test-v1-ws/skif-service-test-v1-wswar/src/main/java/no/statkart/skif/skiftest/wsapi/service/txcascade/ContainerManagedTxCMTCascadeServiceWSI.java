package no.statkart.skif.skiftest.wsapi.service.txcascade;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ContainerManagedTxCMTCascadeServiceWSI extends ServiceWSI {

    void clear() throws ServiceException;

    String get(@WebParam(name = "key") String key) throws ServiceException;
    String put(@WebParam(name = "key") String key, @WebParam(name = "value") String value) throws ServiceException;

    void containerTest1(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
    void containerTest2(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
    void containerTest3(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
    void containerTest4(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;

    void beanTest1(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
    void beanTest2(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
    void beanTest3(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;

}
