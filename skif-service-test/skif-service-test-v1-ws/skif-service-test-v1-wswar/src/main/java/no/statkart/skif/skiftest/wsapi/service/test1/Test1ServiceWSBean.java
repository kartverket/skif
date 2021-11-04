package no.statkart.skif.skiftest.wsapi.service.test1;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.skiftest.wsapi.config.SkifTestWebServiceInjectorConfig;
import no.statkart.skif.skiftest.wsapi.domain.SkifTestContext;
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
@WebService(
        name = "Test1Service",
        serviceName = "Test1ServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/test1")
@Component
public class Test1ServiceWSBean extends SkifWebService<Test1ServiceWSI> implements Test1ServiceWSI {
    @Autowired
    @Resource
    private WebServiceContext ctx;

    private Test1ServiceWSI wsServiceChain;

    public Test1ServiceWSBean() {
        super(Test1ServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = SkifTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public String helloWorld(@WebParam(name = "message") String message, @WebParam(name = "skifTestContext") SkifTestContext skifTestContext) throws ServiceException {
//        System.out.println("here : " + ctx.isUserInRole("Innsyn"));
//        System.out.println("here : " + ctx.isUserInRole("Matrikkelfører"));
//        System.out.println("here : " + ctx.isUserInRole("Posten"));
//        System.out.println("In Test1ServiceWSBean: helloWorld");
        return wsServiceChain.helloWorld(message, skifTestContext);
    }

    @Override
    @WebMethod
    public String helloVersion(@WebParam(name = "message") String message, @WebParam(name = "skifTestContext") SkifTestContext skifTestContext) throws ServiceException {
        return wsServiceChain.helloVersion(message, skifTestContext);
    }
}
