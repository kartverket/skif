package no.statkart.skif.storetest2.domain.kode;

import no.statkart.skif.storetest2.domain.kodeliste.StoreTest2DbKode;

/**
 * En enkel meningsløs kode.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
public class AKode extends StoreTest2DbKode {
    @Override
    public AKodeId getId() {
        return (AKodeId) super.getId();
    }
}
