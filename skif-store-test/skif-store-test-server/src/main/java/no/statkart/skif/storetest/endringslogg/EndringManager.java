package no.statkart.skif.storetest.endringslogg;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.endringslogg.AbstractEndringManager;
import no.statkart.skif.storetest.domain.endringslogg.Endring;

import java.sql.Connection;

/**
 * Genererer endringer for et utvalg objekter.
 *
 * Implementasjonen demonstrerer hvordan en kan sette ekstra informasjon på endring i form av brukernavn for transaksjonen. Se {@link Endring#brukernavn}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EndringManager<E extends Endring> extends AbstractEndringManager<E> {

    private final Provider<ServiceRequestContext> contextProvider;

    @Inject
    public EndringManager(EndringManagerConfiguration endringManagerConfiguration, Provider<ServiceRequestContext> contextProvider, Provider<Connection> connectionProvider, Configuration skifConfiguration) {
        super(endringManagerConfiguration, connectionProvider, skifConfiguration);
        this.contextProvider = contextProvider;
    }

    @Override
    protected void decorateEndring(StoreServer storeServer, E endring) {
        super.decorateEndring(storeServer, endring);

        boolean skalTildelesBrukernavn = true;

        if (skalTildelesBrukernavn) {
            ServiceRequestContext serviceRequestContext = contextProvider.get();
            String principal = serviceRequestContext.getCallerPrincipal().getName();

            endring.setBrukernavn(principal);
        }

    }

}
