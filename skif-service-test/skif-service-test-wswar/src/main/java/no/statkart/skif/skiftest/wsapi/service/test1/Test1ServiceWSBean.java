package no.statkart.skif.skiftest.wsapi.service.test1;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.skiftest.wsapi.config.SkifTestWebServiceInjectorConfig;

import javax.annotation.Resource;
import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.jws.WebParam;
import javax.jws.WebMethod;
import javax.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@WebService(
        name = "Test1Service",
        serviceName = "Test1ServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/test1")
public class Test1ServiceWSBean extends SkifWebService<Test1ServiceWSI> implements Test1ServiceWSI {
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
    public String helloWorld(@WebParam(name = "message") String message) {
//        System.out.println("here : " + ctx.isUserInRole("Innsyn"));
//        System.out.println("here : " + ctx.isUserInRole("Matrikkelfører"));
//        System.out.println("here : " + ctx.isUserInRole("Posten"));
//        System.out.println("In Test1ServiceWSBean: helloWorld");
        return wsServiceChain.helloWorld(message);
    }

    @Override
    @WebMethod
    public String helloVersion(@WebParam(name = "message") String message) {
        return wsServiceChain.helloVersion(message);
    }
}
