package no.statkart.skif.service.ws;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
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
    final private TypeLiteral<T> serviceType;
    final private ServiceRequestScope scope;
    private WebServiceContext webServiceContext;
    private String serviceName;

    @Inject
    public SkifWSInterceptor(TypeLiteral<T> serviceType, ServiceRequestScope scope) {
        this.serviceType = serviceType;
        this.scope = scope;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        scope.enter();
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext();
        serviceRequestContext.setCallerPrincipal(webServiceContext.getUserPrincipal());
        serviceRequestContext.setServicename(serviceName);
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