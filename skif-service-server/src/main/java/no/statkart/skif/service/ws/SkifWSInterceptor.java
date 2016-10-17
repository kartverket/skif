package no.statkart.skif.service.ws;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.annotation.CallId;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.scope.ServiceRequestScope;

import javax.xml.ws.WebServiceContext;
import java.lang.reflect.Method;

/**
 * Starter et ServiceRequestScope for inneværende kall, sender kall videre til neste ProxyHandler i
 * {@code ServiceChain}'en (som krever at ServiceRequestScope) og avslutter ServiceRequestScope etterpå.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifWSInterceptor<T> extends ChainedProxyHandler<T> {
    final private ServiceRequestScope scope;
    final private Provider<Long> callIdProvider;
    private WebServiceContext webServiceContext;
    private String serviceName;

    @Inject
    public SkifWSInterceptor(ServiceRequestScope scope, @CallId Provider<Long> callIdProvider) {
        this.scope = scope;
        this.callIdProvider = callIdProvider;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        scope.enter();
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext(
                webServiceContext.getUserPrincipal(),
                serviceName,
                callIdProvider.get()
        );
        try {
            scope.seed(ServiceRequestContext.class, serviceRequestContext);
            scope.seed(WebServiceContext.class, webServiceContext);
            return chained.invoke(proxy, method, args);
        } finally {
            scope.exit();
        }
    }

    public void setWebServiceContext(WebServiceContext ctx) {
        this.webServiceContext = ctx;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


}