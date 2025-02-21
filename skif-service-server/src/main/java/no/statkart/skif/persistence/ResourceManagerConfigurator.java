package no.statkart.skif.persistence;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.scope.ServiceRequestScoped;

/**
 * Gir mulighet for å velge mellom forskjellige ResourceManager konfigurasjoner for en {@code ServiceRequestContext}.
 * Det kan for eksemple pga ytelse være aktuelt å kunne velge om {@code Connection} objekter skal opprettes via Hibernate
 * {@code Session} eller direkte via en {@code ConnectionFactory}. Når man først har valgt konfigurasjon er det ikke
 * mulig å bytte en en annen konfigurasjon innen for samme {@code ServiceRequestContext}.
 * <p>
 * Design pattern for denne klasse er at klassen er {@code ServiceRequestScoped} og hentes ut via en {@code Provider} i
 * en {@code EJBServiceChain ProxyHandler}. Den første service som kalles for en {@code ServiceRequestContext}
 * Har mulighet for å endre strategi i forhold til default. Etterfølgende proxy handlere må velge kompatible strategier
 * for å unngå å få feil.
 * <p>
 * Dersom en service ikke har noen proxy handler som sette strategien vil den automatisk velge default strategi
 * med mindre annet allerede er valgt. Dermed holder det å legge en proxy handler på de tjenester som eksplisitt
 * trenger en annen strategi enn default.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@ServiceRequestScoped
public class ResourceManagerConfigurator {
    public static final String HIBERNATE = "HIBERNATE";
    public static final String CONNECTION_ONLY = "CONNECTION_ONLY";
    public static final String DEFAULT = HIBERNATE;

    private String strategy;

    public String getStrategy() {
        if (strategy==null) {
            setStrategy(DEFAULT);
        }
        return strategy;
    }

    public void setStrategy(String strategy) {
        if (this.strategy!=null) {
            if (!compatible(strategy)) {
                throw new ImplementationException("Tried to set incompatible strategy:" + strategy);
            }
        }
        this.strategy = strategy;
    }

    private boolean compatible(String strategy) {
        return this.strategy==HIBERNATE || this.strategy == strategy;
    }
}
