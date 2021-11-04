package no.statkart.skif.skiftest.wsapi.service.testb;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.skiftest.wsapi.config.SkifTestWebServiceInjectorConfig;
import no.statkart.skif.skiftest.wsapi.domain.StringList;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@SuppressWarnings("unused")
@WebService(
        name = "BService",
        serviceName = "BServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/testb")
@Component
public class BServiceWSBean extends SkifWebService<BServiceWSI> implements BServiceWSI {
    @Autowired
    @Resource
    private WebServiceContext ctx;

    private BServiceWSI wsServiceChain;


    public BServiceWSBean() {
        super(BServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = SkifTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public String m1(@WebParam(name = "callSpec") StringList callSpec) throws ServiceException {
        return wsServiceChain.m1(callSpec);
    }

    @Override
    @WebMethod
    public String m2(@WebParam(name = "callSpec") StringList callSpec) throws ServiceException {
        return wsServiceChain.m2(callSpec);
    }

    @Override
    @WebMethod
    public String m3(@WebParam(name = "callSpec") StringList callSpec) throws ServiceException {
        return wsServiceChain.m3(callSpec);
    }

}
