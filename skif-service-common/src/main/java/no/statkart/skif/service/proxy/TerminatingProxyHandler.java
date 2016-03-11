package no.statkart.skif.service.proxy;

import no.statkart.skif.exception.ImplementationException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class TerminatingProxyHandler<S> extends ProxyHandler<S> {

    public final ProxyHandler<S> getChained() {
        return null;
    }

    public void setChained(@SuppressWarnings("UnusedParameters") ProxyHandler<S> chained) {
        throw new ImplementationException("This handler terminates the chain and can not be chained");
    }
}
