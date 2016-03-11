package no.statkart.skif.module;

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
