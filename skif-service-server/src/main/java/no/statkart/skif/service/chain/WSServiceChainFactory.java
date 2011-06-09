package no.statkart.skif.service.chain;

/**
 * Interface for factory som lager {@code WSServiceChain} for services av type {@code T}. Denne
 * type {@code ServiceChain} utføres som en del av servicens Web Service implementasjon. Hver Web Service
 * kan ha 2 {@code WSServiceChain}s. En  for WebService interfacet og en for de underliggende EJB interface. Kjeden
 * for WebService interfacet ender normalt i en {@code TerminatingProxyHandler} av type {@code W2DAdapterProxyHandler}
 * som binder de to servicechains sammen. WSServiceChain'en for EJB interfacet ender normalt i en
 * {@code TerminatingProxyHandler} av type {@code EJBCallTypeChooserProxyHandler} som sender kallet videre
 * til EJB implementasjonen eller direkte til implementasjonen.
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface WSServiceChainFactory<T> extends ServiceChainFactory<T> {
}
