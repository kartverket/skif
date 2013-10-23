package no.statkart.skif.storetest.endringslogg;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.endringslogg.AbstractEndring;
import no.statkart.skif.store.endringslogg.AbstractEndringManager;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.domain.endringslogg.BubbleWithRelationEndring;
import no.statkart.skif.storetest.domain.endringslogg.SubTypedBubbleEndring;

import java.sql.Connection;
import java.util.List;

/**
 * Genererer endringer for et utvalg objekter.
 *
 * Implementasjonen demonstrerer hvordan en kan sette ekstra informasjon på endring i form av brukernavn for transaksjonen. Se {@link Endring#brukernavn}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EndringManager extends AbstractEndringManager<Endring> {
    private static List<Class<? extends Endring>> endringsklasser;

    static {
        ImmutableList.Builder<Class<? extends Endring>> builder = ImmutableList.builder();
        builder.add(SimpleEndring.class);
        builder.add(BubbleWithRelationEndring.class);
        builder.add(SubTypedBubbleEndring.class);
        endringsklasser = builder.build();
    }

    private final Provider<ServiceRequestContext> contextProvider;


    @Inject
    public EndringManager(Provider<ServiceRequestContext> contextProvider, Provider<Connection> connectionProvider, Configuration configuration) {
        super(endringsklasser, connectionProvider, configuration);
        this.contextProvider = contextProvider;
    }

    @Override
    protected void decorateEndring(StoreServer storeServer, Endring endring) {
        ServiceRequestContext serviceRequestContext = contextProvider.get();
        String principal = serviceRequestContext.getCallerPrincipal().getName();

        endring.setBrukernavn(principal);

        super.decorateEndring(storeServer, endring);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
