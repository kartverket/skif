package no.statkart.skif.storetest.wsapi.service.histtest;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.wsapi.domain.basic.HistSimpleIdList;
import no.statkart.skif.storetest.wsapi.domain.basic.HistSimpleIdToHistWithRelationIdsMap;
import no.statkart.skif.storetest.wsapi.domain.basic.HistWithRelationIdList;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@WebService(
        name = "HistTestService",
        serviceName = "HistTestServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/histtest")
public class HistTestServiceWSBean extends SkifWebService<HistTestServiceWSI> implements HistTestServiceWSI {

    @Resource
    private WebServiceContext ctx;

    private HistTestServiceWSI wsServiceChain;


    public HistTestServiceWSBean() {
        super(HistTestServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public HistSimpleIdList findHistSimpleIdsForTextUsingJDBC(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findHistSimpleIdsForTextUsingJDBC(text, testSetNummer, storeTestContext);
    }

    @Override
    @WebMethod
    public HistSimpleIdList findHistSimpleIdsForTextUsingHibernate(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findHistSimpleIdsForTextUsingHibernate(text, testSetNummer, storeTestContext);
    }

    @Override
    @WebMethod
    public HistWithRelationIdList findHistWithRelationIdsRelatedToHistSimpleWithText(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException{
        return wsServiceChain.findHistWithRelationIdsRelatedToHistSimpleWithText(text, testSetNummer, snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public HistWithRelationIdList findHistWithRelationIdsWithTextRelatedToHistSimpleId(@WebParam(name = "text") String text, @WebParam(name = "histSimpleId") HistSimpleId histSimpleId, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException{
        return wsServiceChain.findHistWithRelationIdsWithTextRelatedToHistSimpleId(text, histSimpleId, snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public HistSimpleIdToHistWithRelationIdsMap findHistWithRelationIdsWithTextRelatedToHistSimpleIds(@WebParam(name = "text") String text, @WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findHistWithRelationIdsWithTextRelatedToHistSimpleIds(text, histSimpleIds, snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public HistSimpleIdList findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(@WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException{
        return wsServiceChain.findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(histSimpleIds, snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public HistSimpleIdList findHistSimpleIdsAliveAtSnapshotUsingOracleArray(@WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findHistSimpleIdsAliveAtSnapshotUsingOracleArray(histSimpleIds, snapshotVersion,storeTestContext);
    }

}
