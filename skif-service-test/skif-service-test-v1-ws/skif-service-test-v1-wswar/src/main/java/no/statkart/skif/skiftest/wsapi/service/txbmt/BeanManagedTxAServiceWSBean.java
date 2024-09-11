package no.statkart.skif.skiftest.wsapi.service.txbmt;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.skiftest.wsapi.config.SkifTestTxManagementWebServiceInjectorConfig;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@WebService(
        name = "BeanManagedTxAService",
        serviceName = "BeanManagedTxAServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/txbmt")
public class BeanManagedTxAServiceWSBean extends SkifWebService<BeanManagedTxAServiceWSI> implements BeanManagedTxAServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private BeanManagedTxAServiceWSI wsServiceChain;

    public BeanManagedTxAServiceWSBean() {
        super(BeanManagedTxAServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = SkifTestTxManagementWebServiceInjectorConfig.getWebServiceInjector();
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
