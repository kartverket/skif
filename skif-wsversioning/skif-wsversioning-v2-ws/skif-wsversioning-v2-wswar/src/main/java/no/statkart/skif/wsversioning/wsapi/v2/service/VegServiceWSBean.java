package no.statkart.skif.wsversioning.wsapi.v2.service;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.wsversioning.wsapi.v2.config.WSVersioningWebServiceInjectorConfig;
import no.statkart.skif.wsversioning.wsapi.v2.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v2.domain.VegIdList;
import no.statkart.skif.wsversioning.wsapi.v2.exception.ServiceException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

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
