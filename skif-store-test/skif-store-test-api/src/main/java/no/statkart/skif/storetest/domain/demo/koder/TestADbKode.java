package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodelistesupport.DbKodeImpl;
import no.statkart.skif.storetest.domain.kode.TestDbKode;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestADbKode extends DbKodeImpl implements TestDbKode {

    @Override
    public TestADbKodeId getId() {
        return (TestADbKodeId) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
