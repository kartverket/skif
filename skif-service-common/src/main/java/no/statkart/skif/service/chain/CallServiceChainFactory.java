package no.statkart.skif.service.chain;

/**
 * Interface for factories som skal lager {@code CallServiceChain}s for service av type {@code S}. Denne
 * type {@code ServiceChain} utføres i forkant av hvert kall både på klient og server.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface CallServiceChainFactory<S> extends ServiceChainFactory<S> {
}
