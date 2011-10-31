package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.KodelisteImpl;

/**
 * @author Henrik Fredholm
 */
public class StoreTestKodelisteImpl extends KodelisteImpl implements StoreTestKodeliste {
    @Override
    public StoreTestKodelisteIdImpl<?> getId() {
        return (StoreTestKodelisteIdImpl<?>) super.getId();
    }
}
