package no.statkart.skif.storetest.wsapi.service.storetest1;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;

import javax.annotation.Resource;
import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.jws.WebParam;
import javax.jws.WebMethod;
import javax.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 1.1
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
    public String put(@WebParam(name = "key")String key, @WebParam(name = "value")String value) {
        return wsServiceChain.put(key, value);
    }

    @Override
    @WebMethod
    public String get(@WebParam(name = "key")String key) {
        return wsServiceChain.get(key);

    }

    @Override
    @WebMethod
    public String remove(@WebParam(name = "key") String key) {
        return wsServiceChain.remove(key);
    }

    @Override
    @WebMethod
    public void clear() {
        wsServiceChain.clear();

    }
}
