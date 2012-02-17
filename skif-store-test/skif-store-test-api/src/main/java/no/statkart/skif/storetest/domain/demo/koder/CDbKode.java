package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKode;


/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class CDbKode extends StoreTestDbKode {
    @Override
    public CDbKodeId<?> getId() {
        return (CDbKodeId<?>)super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
