package no.statkart.skif.service.chain;

import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Types;
import jakarta.annotation.Nullable;
import no.statkart.skif.SkifUtil;

import java.util.Set;

/**
 * Hjelpeklasse for å binde {@code ServiceChainFactory}s
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServiceChainFactories {
    /**
     * Bind klassen {@code factoryImpl} til ServiceChainFactory-klassen {@code factory} for serviceklassen {@code service}. Denne metoden bruker
     * standard Guice-binding, dvs kun en factoryimplementasjonsklasse kan bindes til den valgte ServiceChainFactory-klassen. For ServiceChainFactory-klasser som skal
     * støtte at et sett av ServiceChainFactory-klasse kan bindes opp må metoden {@link #multibindFactory} anvendes i stedet.
     * til å definerer opp settet.
     */
    public static <S, F extends ServiceChainFactory<S>, FImpl extends F> void bindFactory(Binder binder, Class<F> factory, Class<S> service, Class<FImpl> factoryImpl) {
        TypeLiteral<F> factoryType = SkifUtil.typeLiteral(factory, service);
        TypeLiteral<FImpl> factoryImplType = SkifUtil.typeLiteral(factoryImpl, service);

        binder.bind(factoryType).to(factoryImplType);
        binder.bind(factoryImplType).in(Singleton.class);
    }

    /**
     * Legger til {@code factoryImpl} til settet av ServiceChainFactory-klasser definert av
     * klassen {@code factory} for {@code service}. Implementasjon anvender {@link Multibinder}
     * til å definerer opp settet.
     */
    public static <S, F extends ServiceChainFactory<S>, FImpl extends F> void multibindFactory(Binder binder, Class<F> factory, Class<S> service, @Nullable Class<FImpl> factoryImpl) {
        TypeLiteral<F> factoryType = (TypeLiteral<F>) TypeLiteral.get(Types.newParameterizedType(factory, service));

        Multibinder<F> multibinder = Multibinder.newSetBinder(binder, factoryType);
        if (factoryImpl != null) {
            TypeLiteral<FImpl> factoryImplType = SkifUtil.typeLiteral(factoryImpl, service);
            multibinder.addBinding().to(factoryImplType);
            binder.bind(factoryImplType).in(Singleton.class);
        }

    }

    /**
     * Returnerer en {@code TypeLiteral} som definere et sett av {@code CallChainFactory}-klasser av
     * type {@code factory} for {@code service}. Den returnerte {@code TypeLiteral} kan bl.a. brukes som
     * innput til {@link com.google.inject.Injector#getInstance(com.google.inject.Key)} for å hente ut et slik sett.
     */
    public static <S, F extends ServiceChainFactory<S>> TypeLiteral<Set<F>> makeFactorySetType(Class<F> factory, Class<S> service) {
        TypeLiteral<Set<F>> factorySetType = (TypeLiteral<Set<F>>) TypeLiteral.get(
                Types.newParameterizedType(Set.class, Types.newParameterizedType(factory, service)));
        return factorySetType;
    }

}
