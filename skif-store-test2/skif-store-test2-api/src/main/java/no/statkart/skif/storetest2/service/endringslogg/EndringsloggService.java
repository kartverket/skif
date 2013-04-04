package no.statkart.skif.storetest2.service.endringslogg;

import no.statkart.skif.storetest2.domain.endringslogg.Endring;

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
    public long findSisteEndringsnummer();

    /**
     * Henter alle endringer etter gitt endringsnummer. Endringen med gitt endringsnummer er ikke inkludert.
     *
     * @param endringsnummer endringsnummeret før første endring som skal hentes
     * @param maksAntall     maksimalt antall endringer som skal hentes
     * @return endringene, sortert etter stigende endringsnummer
     */
    public List<Endring> findEndringerEtterEndringsnummer(long endringsnummer, int maksAntall);
}
