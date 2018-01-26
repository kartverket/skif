package no.statkart.skif.wsversioning.wsapi.v1.service;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.wsversioning.wsapi.v1.config.WSVersioningWebServiceInjectorConfig;
import no.statkart.skif.wsversioning.wsapi.v1.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v1.domain.*;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * Implementasjon av {@link StoreServiceWSI}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@WebService(
        name = "LockService",
        serviceName = "LockServiceWS",
        targetNamespace = "http://skif.statkart.no/wsversioning/wsapi/v1/service")
public class LockServiceWSBean extends SkifWebService<LockServiceWSI> implements LockServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private LockServiceWSI wsServiceChain;

    public LockServiceWSBean() {
        super(LockServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = WSVersioningWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    public WSVersioningBubble lock(@WebParam(name="id") WSVersioningBubbleId id, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.lock(id, context);
    }

    @Override
    public WSVersioningBubbleList lockForList(@WebParam(name="ids") WSVersioningBubbleIdList ids, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.lockForList(ids, context);
    }

    @Override
    public void unlock(@WebParam(name="id") WSVersioningBubbleId id, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        wsServiceChain.unlock(id, context);
    }

    @Override
    public void unlockForList(@WebParam(name = "ids") WSVersioningBubbleIdList ids, @WebParam(name = "context") WSVersioningContext context) throws ServiceException {
        wsServiceChain.unlockForList(ids, context);
    }

    @Override
    public boolean isLocked(@WebParam(name="id") WSVersioningBubbleId id, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.isLocked(id, context);
    }


}
