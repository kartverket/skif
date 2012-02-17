package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKode;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class BDbKode extends StoreTestDbKode {

    @Override
    public BDbKodeId getId() {
        return (BDbKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
