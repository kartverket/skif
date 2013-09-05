package no.statkart.skif.wsversioning.wsapi.v1.service;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.wsversioning.wsapi.v1.config.WSVersioningWebServiceInjectorConfig;
import no.statkart.skif.wsversioning.wsapi.v1.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceException;
import no.statkart.skif.wsversioning.wsapi.v1.domain.*;

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
        name = "StoreService",
        serviceName = "StoreServiceWS",
        targetNamespace = "http://skif.statkart.no/wsversioning/wsapi/v1/service")
public class StoreServiceWSBean extends SkifWebService<StoreServiceWSI> implements StoreServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private StoreServiceWSI wsServiceChain;

    public StoreServiceWSBean() {
        super(StoreServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = WSVersioningWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public WSVersioningBubble getObject(@WebParam(name="id") WSVersioningBubbleId id, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.getObject(id, context);
    }

    @Override
    public WSVersioningBubbleList getObjects(@WebParam(name="ids") WSVersioningBubbleIdList ids, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.getObjects(ids, context);
    }

    @Override
    public WSVersioningBubbleList getObjectsIgnoreMissing(@WebParam(name="ids") WSVersioningBubbleIdList ids, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.getObjectsIgnoreMissing(ids, context);
    }

    @Override
    public WSVersioningBubbleIdList getVersions(@WebParam(name="id") WSVersioningBubbleId id, @WebParam(name="start") SnapshotVersion start, @WebParam(name="end") SnapshotVersion end, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.getVersions(id, start, end, context);
    }

    @Override
    public WSVersioningBubbleIdListForWSVersioningBubbleIdsMap getVersionsForList(@WebParam(name="ids") WSVersioningBubbleIdList ids, @WebParam(name="start") SnapshotVersion start, @WebParam(name="end") SnapshotVersion end, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.getVersionsForList(ids, start, end, context);
    }

    @Override
    public WSVersioningBubble lock(@WebParam(name="id") WSVersioningBubbleId id, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.lock(id, context);
    }

    @Override
    public void unlock(@WebParam(name="id") WSVersioningBubbleId id, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        wsServiceChain.unlock(id, context);
    }

    @Override
    public boolean isLocked(@WebParam(name="id") WSVersioningBubbleId id, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.isLocked(id, context);
    }


}
