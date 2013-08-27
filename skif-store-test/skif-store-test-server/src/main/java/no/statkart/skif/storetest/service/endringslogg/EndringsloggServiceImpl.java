package no.statkart.skif.storetest.service.endringslogg;

import com.google.inject.Inject;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.endringslogg.EndringFinder;

import java.util.List;

/**
 * Implementasjon av {@link EndringsloggService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EndringsloggServiceImpl implements EndringsloggService {
    @Inject
    private EndringFinder endringFinder;

    @Override
    public long findSisteEndringsnummer() {
        return endringFinder.findSisteEndringsnummer();
    }

    @Override
    public List<Endring> findEndringerEtterEndringsnummer(long endringsnummer, int maksAntall) {
        return endringFinder.findEndringerEtterEndringsnummer(endringsnummer, maksAntall);
    }
}
