package no.statkart.skif.service.proxy;

/**
 * Denne klassen implementerer en {@link ProxyHandler} som kan inngå som et ikke avsluttende ledd i en
 * {@code ServiceChain}. Etter at klasse har gjort sitt kalles det videre til neste ledd i kjeden.
 *
 * @author Henrik Fredholm
 */
public abstract class ChainedProxyHandler<S> extends ProxyHandler<S> {
    protected ProxyHandler<S> chained;
    protected Class<S> type;


    public ChainedProxyHandler setType(Class<S> type) {
        this.type = type;
        return this;
    }

    public ChainedProxyHandler<S> setChained(ProxyHandler<S> chained) {
        this.chained = chained;
        return this;
    }

    public final ProxyHandler<S> getChained() {
        return chained;
    }
}
