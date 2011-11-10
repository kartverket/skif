package no.statkart.skif.service.chain;

/**
 * Interface for factories som skal lager {@code EJBServiceChain}s for service av type {@code S}. Denne
 * type {@code ServiceChain} utføres som en del av servicens EJB implementasjon. Denne {@code ServiceChain}
 * termineres ved å sende kallet videre til servicens {@code ImplementationServiceChain}.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface EJBServiceChainFactory<S> extends ServiceChainFactory<S> {
}
