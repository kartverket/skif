package no.statkart.skif.storetest.wsapi.service.histtest;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdListForStoreTestBubbleIdsMap;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.SelectionPolygon;
import no.statkart.skif.storetest.wsapi.domain.basic.GeometricElementIdList;
import no.statkart.skif.storetest.wsapi.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.wsapi.domain.basic.HistSimpleIdList;
import no.statkart.skif.storetest.wsapi.domain.basic.HistWithRelationIdList;

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
    public HistSimpleIdList findHistSimpleIdsForTextUsingJDBC(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findHistSimpleIdsForTextUsingJDBC(text, testSetNummer, snapshotVersion,storeTestContext);
    }

    @Override
    @WebMethod
    public HistSimpleIdList findHistSimpleIdsForTextUsingHibernate(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findHistSimpleIdsForTextUsingHibernate(text, testSetNummer, snapshotVersion,storeTestContext);
    }

    @Override
    @WebMethod
    public HistWithRelationIdList findHistWithRelationIdsRelatedToHistSimpleWithText(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findHistWithRelationIdsRelatedToHistSimpleWithText(text, testSetNummer, snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public HistWithRelationIdList findHistWithRelationIdsWithTextRelatedToHistSimpleId(@WebParam(name = "text") String text, @WebParam(name = "histSimpleId") HistSimpleId histSimpleId, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findHistWithRelationIdsWithTextRelatedToHistSimpleId(text, histSimpleId, snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public StoreTestBubbleIdListForStoreTestBubbleIdsMap findHistWithRelationIdsWithTextRelatedToHistSimpleIds(@WebParam(name = "text") String text, @WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findHistWithRelationIdsWithTextRelatedToHistSimpleIds(text, histSimpleIds, snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public HistSimpleIdList findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(@WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(histSimpleIds, snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public HistSimpleIdList findHistSimpleIdsAliveAtSnapshotUsingOracleArray(@WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findHistSimpleIdsAliveAtSnapshotUsingOracleArray(histSimpleIds, snapshotVersion,storeTestContext);
    }

    @Override
    @WebMethod
    public GeometricElementIdList findGeometricElementsWithPointInSelectionPolygon(SelectionPolygon selectionPolygon, SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findGeometricElementsWithPointInSelectionPolygon(selectionPolygon, snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public GeometricElementIdList findGeometricElementsWithPolygonInSelectionPolygon(@WebParam(name = "selectionPolygon") SelectionPolygon selectionPolygon, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findGeometricElementsWithPolygonInSelectionPolygon(selectionPolygon, snapshotVersion, storeTestContext);
    }
}
