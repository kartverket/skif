package no.statkart.skif.storetest.wsapi.service.test;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.MockupTransfer;
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@WebService(
        name = "TestService",
        serviceName = "TestServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/test")
public class TestServiceWSBean extends SkifWebService<TestServiceWSI> implements TestServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private TestServiceWSI wsServiceChain;

    public TestServiceWSBean() {
        super(TestServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    public void saveSnapshotTransfer(@WebParam(name = "transfer") MockupTransfer transfer, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "context") StoreTestContext context) {
        wsServiceChain.saveSnapshotTransfer(transfer, snapshotVersion, context);
    }

    @Override
    public void deleteObject(@WebParam(name = "id") long id, @WebParam(name = "tableName") String tableName, @WebParam(name = "context") StoreTestContext context) {
        wsServiceChain.deleteObject(id, tableName, context);
    }
}
