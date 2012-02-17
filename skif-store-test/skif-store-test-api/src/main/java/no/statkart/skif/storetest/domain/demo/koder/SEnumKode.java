package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKode;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SEnumKode extends StoreTestEnumKode {
    @Override
    public SEnumKodeId getId() {
        return (SEnumKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
