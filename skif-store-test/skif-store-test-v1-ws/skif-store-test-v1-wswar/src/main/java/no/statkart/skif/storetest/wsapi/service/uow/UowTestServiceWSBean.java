package no.statkart.skif.storetest.wsapi.service.uow;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreBubbleTransfer;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basic.SimpleId;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * Service for å støtte UnitOfWork testing
 *
 * @author Henrik Fredholm
 * @since 2.9
 */
@WebService(
        name = "UowTestService",
        serviceName = "UowTestServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/uow")
@Component
public class UowTestServiceWSBean extends SkifWebService<UowTestServiceWSI> implements UowTestServiceWSI {
    @Autowired
    @Resource
    private WebServiceContext ctx;

    private UowTestServiceWSI wsServiceChain;

    public UowTestServiceWSBean() {
        super(UowTestServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public StoreBubbleTransfer findAndLock(@WebParam(name = "bubbleId")  StoreTestBubbleId bubbleId, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.findAndLock(bubbleId, context);
    }

    @Override
    @WebMethod
    public void updateTextInNewTransaction(@WebParam(name = "simpleId")  SimpleId simpleId, @WebParam(name = "text") String text, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.updateTextInNewTransaction(simpleId, text, context);
    }

    @Override
    @WebMethod
    public int antallLaaserForBruker(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.antallLaaserForBruker(context);
    }
}
