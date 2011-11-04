package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodelistesupport.DbKode;
import no.statkart.skif.storetest.domain.kode.StoreTestDbKode;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class ADbKode extends DbKode implements StoreTestDbKode {

    @Override
    public ADbKodeId getId() {
        return (ADbKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
