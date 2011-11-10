package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodeliste.EnumKode;
import no.statkart.skif.storetest.domain.kode.StoreTestEnumKode;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SEnumKode extends EnumKode implements StoreTestEnumKode {
    @Override
    public SEnumKodeId getId() {
        return (SEnumKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
