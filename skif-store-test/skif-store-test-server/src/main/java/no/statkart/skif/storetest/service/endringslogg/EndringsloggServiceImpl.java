package no.statkart.skif.storetest.service.endringslogg;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.endringslogg.AbstractEndringId;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.endringslogg.EndringFinder;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

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
    public long findSisteEndringsnummer(SnapshotVersion snapshotVersion) {
        return endringFinder.findSisteEndringsnummer(snapshotVersion);
    }

    @Override
    public <E extends Endring> List<E> findEndringerEtterEndringsnummer(long endringsnummer, Class<E> endringsklasse, int maksAntall, SnapshotVersion snapshotVersion) {
        return endringFinder.findEndringerEtterEndringsnummerForClass(endringsnummer, endringsklasse, maksAntall, snapshotVersion);
    }
}
