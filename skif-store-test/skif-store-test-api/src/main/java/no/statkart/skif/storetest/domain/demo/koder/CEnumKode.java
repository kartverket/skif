package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodelistesupport.EnumKodeImpl;
import no.statkart.skif.storetest.domain.kode.StoreTestEnumKode;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class CEnumKode extends EnumKodeImpl implements StoreTestEnumKode {
    @Override
    public CEnumKodeId getId() {
        return (CEnumKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
