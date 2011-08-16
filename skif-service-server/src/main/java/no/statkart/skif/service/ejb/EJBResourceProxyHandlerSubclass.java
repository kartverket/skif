package no.statkart.skif.service.ejb;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Henrik Fredholm
 */
public class EJBResourceProxyHandlerSubclass<S> extends EJBResourceProxyHandler<S> {
    @Inject
    public EJBResourceProxyHandlerSubclass(Provider<EJBResourceManager> ejbResourceManagerProvider) {
        super(ejbResourceManagerProvider);
    }
}
