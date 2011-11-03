package no.statkart.skif.storetest.domain.kode;

import no.statkart.skif.store.kodelistesupport.DbKodeImpl;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class StoreTestDbSubclassedKodeImpl extends DbKodeImpl  {

    @Override
    public StoreTestDbSubclassedKodeImplId getId() {
        return (StoreTestDbSubclassedKodeImplId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
