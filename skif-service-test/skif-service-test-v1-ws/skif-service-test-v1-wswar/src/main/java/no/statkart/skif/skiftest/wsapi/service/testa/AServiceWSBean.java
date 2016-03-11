package no.statkart.skif.skiftest.wsapi.service.testa;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.skiftest.wsapi.config.SkifTestWebServiceInjectorConfig;
import no.statkart.skif.skiftest.wsapi.domain.StringList;

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
        name = "AService",
        serviceName = "AServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/testa")
public class AServiceWSBean extends SkifWebService<AServiceWSI> implements AServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private AServiceWSI wsServiceChain;


    public AServiceWSBean() {
        super(AServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = SkifTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public String m1(@WebParam(name = "callSpec") StringList callSpec) {
        return wsServiceChain.m1(callSpec);
    }

    @Override
    @WebMethod
    public String m2(@WebParam(name = "callSpec") StringList callSpec) {
        return wsServiceChain.m2(callSpec);
    }

    @Override
    @WebMethod
    public String m3(@WebParam(name = "callSpec") StringList callSpec) {
        return wsServiceChain.m3(callSpec);
    }

}
