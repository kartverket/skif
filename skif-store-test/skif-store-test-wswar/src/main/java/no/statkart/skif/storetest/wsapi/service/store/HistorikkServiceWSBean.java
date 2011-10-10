package no.statkart.skif.storetest.wsapi.service.store;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdListForStoreTestBubbleIdsMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@WebService(
        name = "HistorikkService",
        serviceName = "HistorikkServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/store")
public class HistorikkServiceWSBean extends SkifWebService<HistorikkServiceWSI> implements HistorikkServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private HistorikkServiceWSI wsServiceChain;

    public HistorikkServiceWSBean() {
        super(HistorikkServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public StoreTestBubbleIdList getVersions(StoreTestBubbleId id, SnapshotVersion start, SnapshotVersion end) {
        return wsServiceChain.getVersions(id, start, end);
    }

    @Override
    @WebMethod
    public StoreTestBubbleIdListForStoreTestBubbleIdsMap getVersionsForList(StoreTestBubbleIdList ids, SnapshotVersion start, SnapshotVersion end) {
        return wsServiceChain.getVersionsForList(ids, start, end);
    }
}
