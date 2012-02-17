package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKode;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ADbKode extends StoreTestDbKode {

    @Override
    public ADbKodeId getId() {
        return (ADbKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
