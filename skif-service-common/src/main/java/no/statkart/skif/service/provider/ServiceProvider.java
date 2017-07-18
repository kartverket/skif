package no.statkart.skif.service.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.chain.OrderedCallServiceChainFactoryList;

/**
 * En Guice provider som produserer service-instanser av type {@code S}. Provideren oppretter service-instansen ved å
 * sette sammen en {@code CallServiceChain} ut fra en ordnet {@code CallServiceChainFactory}-liste. Hver service
 * av type {@code <S>} kan spesifisere sin egen {@code CallServiceChainFactory}-liste slik at services kan ha forskjellig
 * innhold i deres {@code CallServiceChain}.  Hver {@code CallServiceChainFactory} i listen produserer en frakment
 * av {@code CallServiceChain}. Frakmentene settes sammen rekkefølge som deres factory står i listen.
 * <p>
 * {@code CallServiceChainFactory}-listen spesifiseres via en Guice {@code Multibinder<CallServiceChainFactory>} binding
 * som gir tilbake et uordnet sett av {@code CallServiceChainFactory}-instanser. For å unngå å måtte sorterer
 * settet hvergang provideren opprettes brukes en singleton {@link OrderedCallServiceChainFactoryList}-instans. Det
 * er greit å bruke en singleton instans her siden {@code CallServiceChainFactory}-settet er konstant for hver servicetype
 * [@code S}.
 * <p>
 * Guice oppretter en instans av provideren hvergang Guice trenger en ny service-instans av type {@code S} og er dermed
 * styrt av hvilke scope som er anvendt i bindingen av {@code S}.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServiceProvider<S> implements Provider<S> {
    final private TypeLiteral<S> type;
    final private OrderedCallServiceChainFactoryList<S> callServiceChainFactoryList;

    @Inject
    public ServiceProvider(TypeLiteral<S> type, OrderedCallServiceChainFactoryList<S> factoryList) {
        this.type = type;
        this.callServiceChainFactoryList = factoryList;
    }

    @Override
    public S get() {
        //noinspection UnnecessaryLocalVariable
        S instanceOrProxy = callServiceChainFactoryList.buildChain().buildProxy(type);
        return instanceOrProxy;
    }
}
