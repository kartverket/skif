package no.statkart.skif.module;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class ModuleWithStrategy<T extends ModuleStrategy> extends SkifModule {
    protected final ModuleStrategyFactory strategyFactory;
    protected final Class<T> strategyClass;
    private StrategyTuple<T> strategyTuple;
    protected T strategy;

    public ModuleWithStrategy(Class<T> strategyClass, Configuration configuration) {
        super(configuration);
        this.strategyClass = strategyClass;
        this.strategyFactory = moduleConfiguration.getStrategyFactory();
    }

    public ModuleWithStrategy(Class<T> strategyClass, ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
        this.strategyClass = strategyClass;
        this.strategyFactory = moduleConfiguration.getStrategyFactory();
    }

    private StrategyTuple<T> getStrategyTuple() {
        if (strategyTuple ==null) {
            strategyTuple = strategyFactory.createStrategyTuple(this.getClass(), moduleConfiguration.getConfiguration());
        }
        return strategyTuple;
    }

    protected T getStrategy() {
        return getStrategy(moduleConfiguration.getServiceMode());
    }

    protected T setStrategyInstance() {
        return strategy = getStrategy(moduleConfiguration.getServiceMode());
    }


    public T getStrategy(ServiceMode serviceMode) {
        return getStrategyTuple().getStrategy(serviceMode);
    }

    public  ModuleWithStrategy<T> setStrategy(ServiceMode serviceMode, T stragety) {
        getStrategyTuple().setStrategy(serviceMode,  stragety);
        return this;
    }
}

