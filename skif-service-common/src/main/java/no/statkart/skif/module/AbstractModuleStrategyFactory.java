package no.statkart.skif.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.exception.ImplementationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract {@code ModuleStrategyFactory} implementasjon som legger alle prototyper i en map og som bruker
 * superklassen hvis  modulklassen ikke selv har en prototype.
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
