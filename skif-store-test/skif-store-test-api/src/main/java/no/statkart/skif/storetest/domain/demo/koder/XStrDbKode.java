package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodeliste.DbKode;
import no.statkart.skif.storetest.domain.kode.StoreTestDbKode;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class XStrDbKode extends DbKode implements StoreTestDbKode {

    @Override
    public XStrDbKodeId getId() {
        return (XStrDbKodeId) super.getId();
    }
}
