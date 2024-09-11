package no.statkart.skif.wsversioning.wsapi.v2.service;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.wsversioning.wsapi.v2.config.WSVersioningWebServiceInjectorConfig;
import no.statkart.skif.wsversioning.wsapi.v2.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v2.domain.VegIdList;
import no.statkart.skif.wsversioning.wsapi.v2.exception.ServiceException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;

/**
 * Implementasjon av {@link VegServiceWSI}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@WebService(
        name = "VegService",
        serviceName = "VegServiceWS",
        targetNamespace = "http://skif.statkart.no/wsversioning/wsapi/v2/service")
public class VegServiceWSBean extends SkifWebService<VegServiceWSI> implements VegServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private VegServiceWSI wsServiceChain;

    public VegServiceWSBean() {
        super(VegServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = WSVersioningWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public VegIdList findAlleVeger(@WebParam(name = "context") WSVersioningContext context) throws ServiceException {
        return wsServiceChain.findAlleVeger(context);
    }
}
