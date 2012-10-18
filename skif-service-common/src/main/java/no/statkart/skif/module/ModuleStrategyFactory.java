package no.statkart.skif.module;

import no.statkart.skif.config.Configuration;

/**
 * Factory som oppretter {@code ModuleStrategy}-objekter for moduler av subtype {@link ModuleWithStrategy}. Siden
 * strategien som skal brukes for en module avhenger av hvilken {@code ServiceMode} modulen skal anvendes i gir
 * factory'en ut en {@code StrategyTuple} som er istand til å inneholde {@code ModuleStrategy}'er for
 * alle {@code ServiceMode}-verdier.
 * <p/>
 * Configurasjon av factory'en skjer via prototyper. Hver {@code ModuleWithStrategy}-subklasse kan definere sin egen
 * prototype. Når en modul opprettes, hentes ut en ny {@code StrategyTuple}-instans som er en kopi
 * av prototypen definert for modultypen. Modulen kan tilpasse strategien sin uten at prototype for modultypen endres.
 * <p/>
 * Det er implementasjonsavhengig hva som skjer hvis en modul ikke har fått satt noen prototype. Implementasjonen
 * kan enten kaste en ConfigurationException eller bruke en algoritme for å finne en passende protoype, f.eks
 * bruke prototypen til superklassen.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ModuleStrategyFactory {
    public <T extends ModuleStrategy, M extends ModuleWithStrategy<T>> StrategyTuple<T> addPrototype(Class<? extends M> moduleClass, StrategyTuple<T> tuple);

    public <T extends ModuleStrategy, M extends ModuleWithStrategy<T>> StrategyTuple<T> getPrototype(Class<? extends M> moduleClass);

    public <T extends ModuleStrategy, M extends ModuleWithStrategy<T>> StrategyTuple<T> createStrategyTuple(Class<? extends M> moduleClass);

    public <T extends ModuleStrategy, M extends ModuleWithStrategy<T>> StrategyTuple<T> createStrategyTuple(Class<? extends M> moduleClass, Configuration configuration);
}
