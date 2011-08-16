package no.statkart.skif.storetest.wsapi.service.storetest1;

import no.statkart.skif.service.ws.ServiceWSI;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface StoreTest1ServiceWSI extends ServiceWSI {
    public String put(@WebParam(name = "key")String key, @WebParam(name = "value")String value);
    public String get(@WebParam(name = "key")String key);
    public String remove(@WebParam(name = "key") String key);
    public void clear();

}
