package no.statkart.skif.storetest.service.endringslogg;

import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.EndringId;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public interface EndringsloggService extends no.statkart.skif.store.service.EndringsloggService<Endring<?,?>, EndringId<?>> {
}
