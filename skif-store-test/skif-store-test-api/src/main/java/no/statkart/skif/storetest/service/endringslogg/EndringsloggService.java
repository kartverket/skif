package no.statkart.skif.storetest.service.endringslogg;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.endringslogg.Endring;

import java.util.List;

/**
 * Tjeneste for lesing av endringslogg.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public interface EndringsloggService {
    /**
     * Finner siste endringsnummer totalt.
     *
     * @return siste endringenummer
     */
    public long findSisteEndringsnummer(SnapshotVersion snapshotVersion);

    /**
     * Henter alle endringer etter gitt endringsnummer. Endringen med gitt endringsnummer er ikke inkludert.
     *
     * @param endringsnummer endringsnummeret før første endring som skal hentes
     * @param endringsklasse angir filter for endringsklasse
     * @param maksAntall     maksimalt antall endringer som skal hentes
     * @return endringene, sortert etter stigende endringsnummer
     */
    public <E extends Endring> List<E> findEndringerEtterEndringsnummer(long endringsnummer, Class<E> endringsklasse, int maksAntall, SnapshotVersion snapshotVersion);
}
