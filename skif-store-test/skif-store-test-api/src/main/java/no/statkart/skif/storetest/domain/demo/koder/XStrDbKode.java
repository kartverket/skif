package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodeliste.DbKode;
import no.statkart.skif.storetest.domain.kode.StoreTestDbKode;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class XStrDbKode extends StoreTestDbKode {

    @Override
    public XStrDbKodeId getId() {
        return (XStrDbKodeId) super.getId();
    }
}
