package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodeliste.EnumKode;
import no.statkart.skif.storetest.domain.kode.StoreTestEnumKode;


/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class BEnumKode extends StoreTestEnumKode {
    @Override
    public BEnumKodeId getId() {
        return (BEnumKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
