package no.statkart.skif.service.proxy;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class TerminatingProxyHandler<S> extends ProxyHandler<S> {

    public final ProxyHandler<S> getChained() {
        return null;
    }

    public void  setChained(ProxyHandler<S> chained) {
        throw new RuntimeException("Denne handler terminerer kjeden og kan ikke ha flere ledd");
    }
}
