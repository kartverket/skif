package no.statkart.skif.service.ejb;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.chain.ImplementationServiceChainFactory;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;

import javax.ejb.TransactionAttributeType;
import java.lang.reflect.Method;

/**
 * En ProxyHandler for service {@code S} som avgjør om kallet skal gå via EJB eller kan gå direkte ut fra
 * \servicens ejb {@code TransactionAttribute}s . I {@code JEE}-mode brukes kun annotasjoner fra servicens bean
 * implementasjon, mens i {@code SINGLE_VM}-mode brukes servicens implementasjonsklasse dersom servicen
 * ikke har noen bean implementasjon.
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
@Singleton
public class EJBCallTypeChooserProxyHandler<S> extends TerminatingProxyHandler<S> {
    private final ImplementationServiceChainFactory<S> implementationServiceChainFactory;
    private final Provider<EJBCallProxyHandler<S>> ejbProxyHandlerProvider;
    private final Provider<ServiceRequestContext> serviceRequestContextProvider;
    private final EJBAttributesLookup<S> ejbAttributesLookup;

    @Inject
    public EJBCallTypeChooserProxyHandler(ImplementationServiceChainFactory<S> implementationServiceChainFactory, Provider<EJBCallProxyHandler<S>> ejbProxyHandlerProvider, Provider<ServiceRequestContext> serviceRequestContextProvider, EJBAttributesLookup<S> ejbAttributesLookup) {
        this.implementationServiceChainFactory = implementationServiceChainFactory;
        this.ejbProxyHandlerProvider = ejbProxyHandlerProvider;
        this.serviceRequestContextProvider = serviceRequestContextProvider;
        this.ejbAttributesLookup = ejbAttributesLookup;
    }

    @Override
    public Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        final ProxyHandler<S> proxyHandler = calcProxyHandler(method);
        return proxyHandler.invoke(proxy, method, args);
    }

    private ProxyHandler<S> calcProxyHandler(Method method) {
        final TransactionAttributeType txType = ejbAttributesLookup.lookupAttribute(method);
        if (txType == TransactionAttributeType.REQUIRED) {
            final ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();
            if (serviceRequestContext.isTransactional()) {
                return implementationServiceChainFactory.createChain();
            } else {
                return ejbProxyHandlerProvider.get();
            }
        } else if (txType == TransactionAttributeType.REQUIRES_NEW) {
            return ejbProxyHandlerProvider.get();
        } else {
            return implementationServiceChainFactory.createChain();
        }
    }

    @Override
    public S buildProxy(Class<S> type) {
        return super.buildProxy(type);
    }

}
