package no.statkart.skif.module;

import no.statkart.skif.service.module.common.RemoteServiceModuleStrategy;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StrategyTuples {
    public static <T extends ModuleStrategy> StrategyTuple<T> newStrategyTuple() {
        return new StrategyTuple<T>();
    }
    public static <T extends ModuleStrategy> StrategyTuple<T> newStrategyTuple(Class<T> strategyBaseClass) {
        return new StrategyTuple<T>(strategyBaseClass);
    }

}
