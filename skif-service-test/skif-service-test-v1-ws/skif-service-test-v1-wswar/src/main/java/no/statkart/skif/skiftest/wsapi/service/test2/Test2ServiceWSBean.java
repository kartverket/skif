package no.statkart.skif.skiftest.wsapi.service.test2;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.skiftest.wsapi.config.SkifTestWebServiceInjectorConfig;
import no.statkart.skif.skiftest.wsapi.domain.A;
import no.statkart.skif.skiftest.wsapi.domain.B;
import no.statkart.skif.skiftest.wsapi.domain.SkifTestContext;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@WebService(
        name = "Test2Service",
        serviceName = "Test2ServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/test2")
public class Test2ServiceWSBean extends SkifWebService<Test2ServiceWSI> implements Test2ServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private Test2ServiceWSI wsServiceChain;

    public Test2ServiceWSBean() {
        super(Test2ServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = SkifTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public B a2B(@WebParam(name = "a") A a,  @WebParam(name="skifTestContext")SkifTestContext skifTestContext) {
        return wsServiceChain.a2B(a, skifTestContext);
    }

}
