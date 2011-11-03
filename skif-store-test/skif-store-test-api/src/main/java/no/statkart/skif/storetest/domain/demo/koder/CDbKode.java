package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.storetest.domain.kode.StoreTestDbKode;
import no.statkart.skif.storetest.domain.kode.StoreTestDbSubclassedKodeImpl;
import no.statkart.skif.storetest.domain.kode.StoreTestDbSubclassedKodeImplId;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class CDbKode extends StoreTestDbSubclassedKodeImpl implements StoreTestDbKode {
    @Override
    public CDbKodeId<?> getId() {
        return (CDbKodeId<?>)super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
