package no.statkart.skif.storetest2.domain.eierskap;

import no.statkart.skif.storetest2.domain.kodeliste.StoreTest2EnumKode;

/**
 * Enkel enumkode.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class EiendomstypeKode extends StoreTest2EnumKode {
    @Override
    public EiendomstypeKodeId getId() {
        return (EiendomstypeKodeId) super.getId();
    }
}
