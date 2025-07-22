package no.statkart.skif.wsversioning.wsapi.v2.service;

import com.google.inject.Injector;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.wsversioning.wsapi.v2.config.WSVersioningWebServiceInjectorConfig;
import no.statkart.skif.wsversioning.wsapi.v2.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubble;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubbleId;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubbleIdList;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubbleIdListForWSVersioningBubbleIdsMap;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubbleList;
import no.statkart.skif.wsversioning.wsapi.v2.exception.ServiceException;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Implementasjon av {@link StoreServiceWSI}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@WebService(
        name = "StoreService",
        serviceName = "StoreServiceWS",
        targetNamespace = "http://skif.statkart.no/wsversioning/wsapi/v2/service")
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
    public WSVersioningBubbleIdList getVersions(@WebParam(name="id") WSVersioningBubbleId id, @WebParam(name="start") XMLGregorianCalendar start, @WebParam(name="end") XMLGregorianCalendar end, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.getVersions(id, start, end, context);
    }

    @Override
    public WSVersioningBubbleIdListForWSVersioningBubbleIdsMap getVersionsForList(@WebParam(name="ids") WSVersioningBubbleIdList ids, @WebParam(name="start") XMLGregorianCalendar start, @WebParam(name="end") XMLGregorianCalendar end, @WebParam(name="context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.getVersionsForList(ids, start, end, context);
    }

}
