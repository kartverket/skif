package no.statkart.skif.service.ws;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import jakarta.xml.ws.WebServiceContext;
import no.statkart.skif.service.chain.WSServiceChainFactory;

/**
 * Factory for å få tak i {@code WSServiceChain} for Web Service av type {@code <W>}.
 * <p>
 * For hver gang {@link WebServiceImplementationFactory#getService(jakarta.xml.ws.WebServiceContext, Class)} kalles
 * oppretter factoryen en ny {@code WSServiceChain} med tilhørende proxy av type {@code W}.
 * <p>
 * {@code WSServiceChain}'en konstrueres ved å sette sammen en en {@code SkifWSInterceptor<W>} med ProxyHandlere
 * fra {@code WSServiceChainFactory}.
 *
 * @since 2.0
 * @author Henrik Fredholm
 */
public class WebServiceImplementationFactoryImpl<W> implements WebServiceImplementationFactory<W> {
    final TypeLiteral<W> type;
    final SkifWSInterceptor<W> skifWSInterceptor;
    final WSServiceChainFactory<W> wsServiceChainFactory;

    @Inject
    public WebServiceImplementationFactoryImpl(TypeLiteral<W> type, SkifWSInterceptor<W> skifWSInterceptor, WSServiceChainFactory<W> wsServiceChainFactory) {
        this.type = type;
        this.skifWSInterceptor = skifWSInterceptor;
        this.wsServiceChainFactory = wsServiceChainFactory;
    }

    @Override
    public W getService(WebServiceContext ctx, Class webServiceImpl) {
        skifWSInterceptor.setWebServiceContext(ctx);
        skifWSInterceptor.setServiceName(buildServiceName(webServiceImpl));
        skifWSInterceptor.setChained(wsServiceChainFactory.createChain());
        return skifWSInterceptor.buildProxy(type);
    }


    private String buildServiceName(Class impl) {
        jakarta.jws.WebService webService = (jakarta.jws.WebService) impl.getAnnotation(jakarta.jws.WebService.class);
        if (webService.name() != null && !webService.name().trim().equals("")) {
            return impl.getPackage().getName() + "." + webService.name();
        } else {
            return impl.getName();
        }
    }

}
