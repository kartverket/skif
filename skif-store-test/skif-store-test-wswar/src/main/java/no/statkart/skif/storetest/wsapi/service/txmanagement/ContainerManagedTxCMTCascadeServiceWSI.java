package no.statkart.skif.storetest.wsapi.service.txmanagement;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface ContainerManagedTxCMTCascadeServiceWSI extends ServiceWSI {
    public void clear() throws ServiceException;
    public String get(@WebParam(name = "key") String key) throws ServiceException;
    public String put(@WebParam(name = "key") String key, @WebParam(name = "value") String value) throws ServiceException;
    public void containerTest1(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
    public void containerTest2(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
    public void containerTest3(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
    public void containerTest4(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
    public void beanTest1(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
    public void beanTest2(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
    public void beanTest3(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException;
}
