package no.statkart.skif.storetest.domain.koder;

import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKode;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeId;

/**
 * En veldig enkel rett frem kode uten historikk.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class SimpleEnumKode extends StoreTestEnumKode {
    private static final long serialVersionUID = 1L;

    @Override
    public SimpleEnumKodeId getId() {
        return (SimpleEnumKodeId) super.getId();
    }
}
