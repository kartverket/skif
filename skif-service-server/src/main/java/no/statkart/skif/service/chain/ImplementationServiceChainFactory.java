package no.statkart.skif.service.chain;

/**
 * Interface for factories som skal lager {@code ImplementationServiceChain}s for service av type {@code S}. Denne
 * type {@code ServiceChain} utføres i forkant av hvert kall til service-implementasjonen på server.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ImplementationServiceChainFactory<S> extends ServiceChainFactory<S> {
}
