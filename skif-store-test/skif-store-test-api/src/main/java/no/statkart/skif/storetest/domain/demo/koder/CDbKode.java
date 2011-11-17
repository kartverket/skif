package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodeliste.DbKode;
import no.statkart.skif.storetest.domain.kode.StoreTestDbKode;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class CDbKode extends DbKode implements StoreTestDbKode {
    @Override
    public CDbKodeId<?> getId() {
        return (CDbKodeId<?>)super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
