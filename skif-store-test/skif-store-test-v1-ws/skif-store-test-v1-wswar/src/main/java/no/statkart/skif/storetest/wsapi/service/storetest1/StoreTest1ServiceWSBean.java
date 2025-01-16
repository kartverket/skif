package no.statkart.skif.storetest.wsapi.service.storetest1;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import jakarta.annotation.Resource;
import jakarta.annotation.PostConstruct;
import jakarta.jws.WebService;
import jakarta.jws.WebParam;
import jakarta.jws.WebMethod;
import jakarta.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@WebService(
        name = "StoreTest1Service",
        serviceName = "StoreTest1ServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/storetest1")
public class StoreTest1ServiceWSBean extends SkifWebService<StoreTest1ServiceWSI> implements StoreTest1ServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private StoreTest1ServiceWSI wsServiceChain;

    public StoreTest1ServiceWSBean() {
        super(StoreTest1ServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public String put(@WebParam(name = "key")String key, @WebParam(name = "value")String value)  throws ServiceException {
        return wsServiceChain.put(key, value);
    }

    @Override
    @WebMethod
    public String get(@WebParam(name = "key")String key) throws ServiceException {
        return wsServiceChain.get(key);

    }

    @Override
    @WebMethod
    public String remove(@WebParam(name = "key") String key) throws ServiceException {
        return wsServiceChain.remove(key);
    }

    @Override
    @WebMethod
    public void clear() throws ServiceException {
        wsServiceChain.clear();

    }

    @Override
    @WebMethod
    public String putThatFails(@WebParam(name = "key")String key, @WebParam(name = "value")String value) throws ServiceException {
        return wsServiceChain.putThatFails(key, value);
    }

    @Override
    @WebMethod
    public String putViaJDBCConnection(@WebParam(name = "key")String key, @WebParam(name = "value")String value) throws ServiceException {
        return wsServiceChain.putViaJDBCConnection(key, value);
    }

}

