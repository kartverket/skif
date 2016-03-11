package no.statkart.skif.service.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.chain.EJBServiceChainFactory;
import no.statkart.skif.service.chain.ImplementationServiceChainFactory;
import no.statkart.skif.service.proxy.ProxyHandler;

/**
 * En Guice provider som returnerer en service proxy av type {@code S} som sender kallet gjennom servicens
 * {@code EJBServiceChain} og {@code ImplementationServiceChain}. Provideren bruke en {@code EJBServiceChainFactory<S>}
 * og en {@code ImplementationServiceChainFactory} til å lage de ProxyHandlere som utgjøre ServiceChain'en. Hver
 * service av type {@code S} har sin egen {@code EJBServiceChainFactory<S>} og
 * {@code ImplementationServiceChainFactory<S>} implementasjoner og kan derfor ha forskjellige innhold i deres
 * {@code EJBServiceChain}s om ønskelig.
 * <p>
 * Denne provider skal kun brukes av skif-rammeverket og skal ikke brukes direkte fra annen kode.
 * Provideren bindes inn av skif-rammeverket med en egen Guice key {@link no.statkart.skif.service.annotation.EJBRef}
 * for type {@code S} slik at den ikke kommer i konflikt med standard bindingen for  type {@code S}.
 * <p>
 * Servicens {@code EJBServiceChain} implementerer et abstraksjonlag som gjør det mulig å implementere services som
 * virker likt både med og uten en applikasjonsserver. Instanser fra denne provider brukes litt forskjellig i
 * {@code JEE} og {@code SINGLE_VM} mode siden applikasonstjenerens ejb-implementasjon automatisk gjør en del arbeide
 * rundt transaksjonshåndtering som må gjøres manulet i {@code SINGLE_VM}-mode. Derfor har de to modes forskjellige
 * {@code EJBServiceChainFactory}-implementasjoner.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class EJBServiceChainProvider<S> implements Provider<S> {
    final private TypeLiteral<S> type;
    final private EJBServiceChainFactory<S> ejbServiceChainFactory;
    final private ImplementationServiceChainFactory<S> implementationServiceChainFactory;

    @Inject
    public EJBServiceChainProvider(TypeLiteral<S> type, EJBServiceChainFactory<S> ejbServiceChainFactory, ImplementationServiceChainFactory<S> implementationServiceChainFactory) {
        this.type = type;
        this.ejbServiceChainFactory = ejbServiceChainFactory;
        this.implementationServiceChainFactory = implementationServiceChainFactory;
    }

    @Override
    public S get() {
        final ProxyHandler<S> implChain = implementationServiceChainFactory.createChain();
        S proxy = ejbServiceChainFactory.extendChain(implChain).buildProxy(type);
        return proxy;
    }
}
