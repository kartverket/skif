package no.statkart.skif.service.chain;

import com.google.inject.Binder;
import no.statkart.skif.exception.ImplementationException;

/**
 * En spesifikasjon som angir en ServiceChainFactory klasse med tilhørende Guice bindinger som må til for å binde opp
 * ProxyHandlere som factoryen anvender for hver service.
 * <p>
 * Klassen har også en hjelpemetode {@link #requireBinding(com.google.inject.Binder, Class)} som gjør de mulig å angi
 * andre bindinger som factoryen avhenger av og som allerede må være bundet opp.
 *
 * @author Henrik Fredholm
 */
public abstract class FactorySpecification<T extends ServiceChainFactory> implements Cloneable {
    protected Class<? extends T> factoryClass;

    /**
     * Metode for å binde opp ProxyHandlere som inngår i ServiceChain'en som factoryen produserer.
     */
    public abstract <S> void bindProxyHandlersForService(Binder binder, Class<S> service);

    protected FactorySpecification(Class<? extends T> factoryClass) {
        this.factoryClass = factoryClass;
    }

    public Class<? extends T> getFactoryClass() {
        return factoryClass;
    }

    protected void requireBinding(Binder binder, Class<?> type) {
        binder.getProvider(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FactorySpecification<T> clone() {
        try {
            return (FactorySpecification<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new ImplementationException(e);
        }
    }
}
