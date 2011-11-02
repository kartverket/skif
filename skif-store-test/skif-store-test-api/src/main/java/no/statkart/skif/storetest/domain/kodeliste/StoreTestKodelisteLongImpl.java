package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.KodelisteImpl;

/**
 * @author Henrik Fredholm
 */
public class StoreTestKodelisteLongImpl extends KodelisteImpl implements StoreTestKodelisteLong {
    @Override
    public StoreTestKodelisteImplLongId<?> getId() {
        return (StoreTestKodelisteImplLongId<?>) super.getId();
    }
}
