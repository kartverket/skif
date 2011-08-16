package no.statkart.skif.service.ejb;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.chain.ImplementationServiceChainFactory;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;
import sun.net.ResourceManager;

import javax.ejb.TransactionAttributeType;
import java.lang.reflect.Method;

/**
 * En EJBServiceChain ProxyHandler som har ansvar for å avslutte ressurser som har vært i bruk under servicekallet
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class EJBResourceProxyHandler<S> extends ChainedProxyHandler<S> {
    private final Provider<EJBResourceManager> ejbResourceManagerProvider;

    @Inject
    public EJBResourceProxyHandler(Provider<EJBResourceManager> ejbResourceManagerProvider) {
        this.ejbResourceManagerProvider = ejbResourceManagerProvider;
    }

    @Override
    public Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        EJBResourceManager ejbResourceManager = ejbResourceManagerProvider.get();
        try {
            Object result = chained.invoke(proxy, method, args);
            if (ejbResourceManager != null) {
                ejbResourceManager.complete();
            }
            return result;
        } catch (Throwable e) {
            if (ejbResourceManager != null) {
                ejbResourceManager.abort();
            }
            throw e;
        }
    }
}
