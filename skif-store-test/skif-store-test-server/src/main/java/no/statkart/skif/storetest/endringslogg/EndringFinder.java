package no.statkart.skif.storetest.endringslogg;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import no.statkart.skif.store.endringslogg.AbstractEndringFinder;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import org.hibernate.Session;

/**
 * Finder for endringslogg.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Singleton
public class EndringFinder extends AbstractEndringFinder<Endring> {
    @Inject
    public EndringFinder() {
        super(Endring.class);
    }
}
