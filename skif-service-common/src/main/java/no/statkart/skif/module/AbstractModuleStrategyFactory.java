package no.statkart.skif.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.exception.ImplementationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract {@code ModuleStrategyFactory} implementasjon som legger alle prototyper i en map og som bruker
 * superklassen hvis  modulklassen som brukes som nøkkel ikke selv har en prototype.
 *
 * <P/>For hver modulklasse anngis en implementasjonsklassen, som må være av en gitt subtype, som skal brukes for
 * konfigurasjon av pågjeldende modul. Det er mulig å ha en egen implementasjonsklasser for hver
 * {@link no.statkart.skif.ServiceMode} systemet kan kjøres i. Den faktiske implementasjonsklassen som anvendes for en
 * {@code ServiceMode} avledes ved å legge til navnet på {@code ServideMode} til implementasjonsklassens navn. Hvis
 * denne klassen ikke finnes brukes implementasjonsklassen istedet.  Klassene lastest via refelction fordi
 * klasser som brukes for SingleVm ikke er tilgjengelig på classpath for klienter som kun kan kjøre i JEE mode.
 *
 * <P/>For eksempel angir {@code RemoteServiceModuleStrategy.class} at klassen
 * {@code RemoteServiceModuleStrategyJEE.class} skal anvendes for {@link no.statkart.skif.ServiceMode#JEE} og at
 * {@code RemoteServiceModuleStrategySinglevm} skal anvendes for  {@link no.statkart.skif.ServiceMode#SINGLE_VM}.
 *
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class AbstractModuleStrategyFactory implements ModuleStrategyFactory {
    protected  ServiceMode serviceMode = ServiceMode.JEE;
    protected Map<Class<? extends ModuleWithStrategy<?>>, StrategyTuple<?>> strategyTupleMap = new HashMap<Class<? extends ModuleWithStrategy<?>>, StrategyTuple<?>>();


    public AbstractModuleStrategyFactory() {
    }


    public ServiceMode getServiceMode() {
        return serviceMode;
    }

    public void setServiceMode(ServiceMode serviceMode) {
        this.serviceMode = serviceMode;
    }

    @Override
    public <T extends ModuleStrategy, M extends ModuleWithStrategy<T>> StrategyTuple<T> addPrototype(Class<? extends M> moduleClass, StrategyTuple<T> tuple) {
        strategyTupleMap.put(moduleClass, tuple);
        return tuple;
    }

    @Override
    public <T extends ModuleStrategy, M extends ModuleWithStrategy<T>> StrategyTuple<T> getPrototype(Class<? extends M> moduleClass) {
        final StrategyTuple<T> strategyTuple = (StrategyTuple<T>) strategyTupleMap.get(moduleClass);
        return strategyTuple;
    }

    @Override
    public <T extends ModuleStrategy, M extends ModuleWithStrategy<T>> StrategyTuple<T> createStrategyTuple(Class<? extends M> moduleClass) {
        StrategyTuple<T> prototype=null;
        for (Class<?> mClass = moduleClass; mClass!=ModuleWithStrategy.class; mClass = mClass.getSuperclass()) {
            prototype = (StrategyTuple<T>) strategyTupleMap.get(mClass);
            if (prototype!=null) {
                break;
            }
        }
        if (prototype==null) {
            throw new ImplementationException("Could not find ModuleStrategy prototype for module: " + moduleClass.getName());
        }

        return prototype.clone();
    }

    @Override
    public <T extends ModuleStrategy, M extends ModuleWithStrategy<T>> StrategyTuple<T> createStrategyTuple(Class<? extends M> moduleClass, Configuration configuration ) {
        StrategyTuple<T> clone = createStrategyTuple(moduleClass);
        clone.setConfiguration(configuration);
        return clone;
    }
 }
