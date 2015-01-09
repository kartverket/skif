package no.statkart.skif.storetest.wsapi.service.exceptiontest;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

@WebService(
        name = "ExceptionTestService",
        serviceName = "ExceptionTestServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest")
public class ExceptionTestServiceWSBean extends SkifWebService<ExceptionTestServiceWSI> implements ExceptionTestServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private ExceptionTestServiceWSI wsServiceChain;

    public ExceptionTestServiceWSBean() {
        super(ExceptionTestServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public void throwImplementationException(@WebParam(name = "message", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") String message, @WebParam(name = "storeTestContext", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") StoreTestContext storeTestContext ) throws ServiceException {
        wsServiceChain.throwImplementationException(message, storeTestContext);
    }

    @Override
    @WebMethod
    public void throwFinderException(@WebParam(name = "message", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") String message, @WebParam(name = "storeTestContext", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") StoreTestContext storeTestContext) throws ServiceException {
        wsServiceChain.throwFinderException(message, storeTestContext);
    }

    @Override
    @WebMethod
    public void throwAttemptDeleteException(@WebParam(name = "id", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") StoreTestBubbleId id, @WebParam(name = "storeTestContext", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") StoreTestContext storeTestContext) throws ServiceException {
        wsServiceChain.throwAttemptDeleteException(id, storeTestContext);
    }

    @Override
    @WebMethod
    public void throwLockedException(@WebParam(name = "id", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") StoreTestBubbleId id, @WebParam(name = "owner", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") String owner, @WebParam(name = "expires", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") Timestamp expires, @WebParam(name = "storeTestContext", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") StoreTestContext storeTestContext) throws ServiceException {
        wsServiceChain.throwLockedException(id, owner, expires, storeTestContext);
    }

    @Override
    @WebMethod
    public void throwObjectNotFoundException(@WebParam(name = "id", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") StoreTestBubbleId id, @WebParam(name = "storeTestContext", targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/exceptiontest") StoreTestContext storeTestContext) throws ServiceException {
        wsServiceChain.throwObjectNotFoundException(id, storeTestContext);
    }
}
