package no.statkart.skif.wsversioning.wsapi.v1.service;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.wsversioning.wsapi.v1.config.WSVersioningWebServiceInjectorConfig;
import no.statkart.skif.wsversioning.wsapi.v1.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v1.domain.GateIdList;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;

/**
 * Implementasjon av {@link GateServiceWSI}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@WebService(
        name = "GateService",
        serviceName = "GateServiceWS",
        targetNamespace = "http://skif.statkart.no/wsversioning/wsapi/v1/service")
public class GateServiceWSBean extends SkifWebService<GateServiceWSI> implements GateServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private GateServiceWSI wsServiceChain;

    public GateServiceWSBean() {
        super(GateServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = WSVersioningWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public GateIdList findAlleGater(@WebParam(name = "context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.findAlleGater(context);
    }
}
