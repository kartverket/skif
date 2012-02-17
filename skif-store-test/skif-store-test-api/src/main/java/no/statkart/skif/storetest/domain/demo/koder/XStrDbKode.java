package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKode;

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
