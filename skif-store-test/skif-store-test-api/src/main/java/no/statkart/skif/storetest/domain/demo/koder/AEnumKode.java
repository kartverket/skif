package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodelistesupport.EnumKode;
import no.statkart.skif.storetest.domain.kode.StoreTestEnumKode;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class AEnumKode extends EnumKode implements StoreTestEnumKode {
    @Override
    public AEnumKodeId getId() {
        return (AEnumKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
