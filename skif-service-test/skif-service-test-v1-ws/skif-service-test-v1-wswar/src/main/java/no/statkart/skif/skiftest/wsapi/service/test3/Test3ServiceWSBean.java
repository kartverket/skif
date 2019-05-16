package no.statkart.skif.skiftest.wsapi.service.test3;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.skiftest.wsapi.config.SkifTestWebServiceInjectorConfig;
import no.statkart.skif.skiftest.wsapi.domain.A;
import no.statkart.skif.skiftest.wsapi.domain.B;
import no.statkart.skif.skiftest.wsapi.domain.SkifTestContext;
import no.statkart.skif.skiftest.wsapi.exception.SimpleException;

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
@WebService(
        name = "Test3Service",
        serviceName = "Test3ServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/test3")
public class Test3ServiceWSBean extends SkifWebService<Test3ServiceWSI> implements Test3ServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private Test3ServiceWSI wsServiceChain;

    public Test3ServiceWSBean() {
        super(Test3ServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = SkifTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public A b2A(@WebParam(name = "b") B b, @WebParam(name="skifTestContext")SkifTestContext skifTestContext) {
        return wsServiceChain.b2A(b, skifTestContext);
    }

    @Override
    public String testThrowException(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name="message") String message, @WebParam(name="skifTestContext")SkifTestContext skifTestContext) throws SimpleException {
        return wsServiceChain.testThrowException(exceptionClass, message, skifTestContext);
    }
}

