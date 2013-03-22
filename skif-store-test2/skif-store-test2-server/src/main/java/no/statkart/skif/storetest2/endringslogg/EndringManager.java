package no.statkart.skif.storetest2.endringslogg;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.endringslogg.AbstractEndring;
import no.statkart.skif.store.endringslogg.AbstractEndringManager;

import java.util.List;

/**
 * Genererer endringer for et utvalg objekter.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EndringManager extends AbstractEndringManager {
    private static List<Class<? extends AbstractEndring>> endringsklasser;

    static {
        ImmutableList.Builder<Class<? extends AbstractEndring>> builder = ImmutableList.builder();
//        builder.add(ParrentBubbleEndring.class);
//        builder.add(ChildBubbleEndring.class);
        endringsklasser = builder.build();
    }

    @Inject
    public EndringManager(Provider<ServiceRequestContext> contextProvider) {
        super(contextProvider, endringsklasser);
    }
}
