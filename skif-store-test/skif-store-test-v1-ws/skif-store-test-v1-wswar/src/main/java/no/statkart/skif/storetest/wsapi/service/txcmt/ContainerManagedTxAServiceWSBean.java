package no.statkart.skif.storetest.wsapi.service.txcmt;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@WebService(
        name = "ContainerManagedTxAService",
        serviceName = "ContainerManagedTxAServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/txcmt")
public class ContainerManagedTxAServiceWSBean extends SkifWebService<ContainerManagedTxAServiceWSI> implements ContainerManagedTxAServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private ContainerManagedTxAServiceWSI wsServiceChain;

    public ContainerManagedTxAServiceWSBean() {
        super(ContainerManagedTxAServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    public void clear() throws ServiceException {
        wsServiceChain.clear();
    }

    @Override
    public String get(@WebParam(name = "key") String key) throws ServiceException {
        return wsServiceChain.get(key);
    }

    @Override
    public String put(@WebParam(name = "key") String key, @WebParam(name = "value") String value) throws ServiceException {
        return wsServiceChain.put(key, value);
    }

    @Override
    public void multiPut(@WebParam(name = "key1") String key1, @WebParam(name = "value1") String value1, @WebParam(name = "key2") String key2, @WebParam(name = "value2") String value2) throws ServiceException {
        wsServiceChain.multiPut(key1, value1, key2, value2);
    }
}
