package no.statkart.skif.module;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StrategyTuples {
    public static <T extends ModuleStrategy> StrategyTuple<T> newStrategyTuple() {
        return new StrategyTuple<>();
    }
    public static <T extends ModuleStrategy> StrategyTuple<T> newStrategyTuple(Class<? extends T> strategyBaseClass) {
        return new StrategyTuple<>(strategyBaseClass);
    }

}
