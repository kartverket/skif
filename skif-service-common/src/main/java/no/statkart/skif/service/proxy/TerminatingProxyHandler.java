package no.statkart.skif.service.proxy;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class TerminatingProxyHandler<S> extends ProxyHandler<S> {

    public final ProxyHandler<S> getChained() {
        return null;
    }
}
