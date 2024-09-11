package no.statkart.skif.storetest.wsapi.service.kodeliste;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@SuppressWarnings("unused")

@WebService(
        name = "KodelisteService",
        serviceName = "KodelisteServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/kodeliste")
public class KodelisteServiceWSBean extends SkifWebService<KodelisteServiceWSI> implements KodelisteServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private KodelisteServiceWSI wsServiceChain;

    public KodelisteServiceWSBean() {
        super(KodelisteServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    public KodelisteTransfer getKodelister(@WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.getKodelister(snapshotVersion, context);
    }
}

