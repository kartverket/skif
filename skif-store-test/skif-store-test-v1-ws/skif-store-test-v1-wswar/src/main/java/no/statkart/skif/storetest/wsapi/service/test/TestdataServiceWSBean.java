package no.statkart.skif.storetest.wsapi.service.test;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * @author Roar Ingebrigtsen
 * @author Tor Egil R. Strand
 * @since 2.0
 */
@WebService(
        name = "TestdataService",
        serviceName = "TestdataServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/test")
public class TestdataServiceWSBean extends SkifWebService<TestdataServiceWSI> implements TestdataServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private TestdataServiceWSI wsServiceChain;

    public TestdataServiceWSBean() {
        super(TestdataServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    /** @since 2.1 */
    @Override
    public TestNumber getNextTestNumber(@WebParam(name = "context") StoreTestContext context) {
        return wsServiceChain.getNextTestNumber(context);
    }

    /** @since 2.1 */
    @Override
    public TestNumber getTestNumber0(@WebParam(name = "context") StoreTestContext context) {
        return wsServiceChain.getTestNumber0(context);
    }

    /** @since 2.1 */
    @Override
    public void saveAll(@WebParam(name = "snapshotTransfers") MockupSnapshotMap snapshotTransfers, @WebParam(name = "context") StoreTestContext context) {
        wsServiceChain.saveAll(snapshotTransfers, context);
    }

    /** @since 2.1 */
    @Override
    public void saveSnapshotTransfer(@WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "mockupTransfer") MockupTransfer mockupTransfer, @WebParam(name = "context") StoreTestContext context) {
        wsServiceChain.saveSnapshotTransfer(snapshotVersion, mockupTransfer, context);
    }

    @Override
    public void deleteObject(@WebParam(name = "id") long id, @WebParam(name = "tableName") String tableName, @WebParam(name = "context") StoreTestContext context) {
        wsServiceChain.deleteObject(id, tableName, context);
    }
}
