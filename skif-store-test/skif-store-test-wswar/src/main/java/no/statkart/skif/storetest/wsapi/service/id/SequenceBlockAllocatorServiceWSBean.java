package no.statkart.skif.storetest.wsapi.service.id;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;

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
        name = "SequenceBlockAllocatorService",
        serviceName = "SequenceBlockAllocatorServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/id")
public class SequenceBlockAllocatorServiceWSBean extends SkifWebService<SequenceBlockAllocatorServiceWSI> implements SequenceBlockAllocatorServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private SequenceBlockAllocatorServiceWSI wsServiceChain;

    public SequenceBlockAllocatorServiceWSBean() {
        super(SequenceBlockAllocatorServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }


    @Override
    public long allocateSequenceBlock(@WebParam(name = "sequenceName") String sequenceName, @WebParam(name = "blockSize") int blockSize) {
        return wsServiceChain.allocateSequenceBlock(sequenceName, blockSize);
    }
}
