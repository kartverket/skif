package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodelistesupport.DbKodeImpl;
import no.statkart.skif.storetest.domain.kode.StoreTestDbKode;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class BDbKode extends DbKodeImpl implements StoreTestDbKode {

    @Override
    public BDbKodeId getId() {
        return (BDbKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
