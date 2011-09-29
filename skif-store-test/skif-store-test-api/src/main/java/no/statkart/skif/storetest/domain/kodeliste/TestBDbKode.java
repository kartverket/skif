package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.DbKodeImpl;
import no.statkart.skif.storetest.domain.TestDbKode;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBDbKode extends DbKodeImpl implements TestDbKode {

    @Override
    public TestBDbKodeId getId() {
        return (TestBDbKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
