package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.kodelistesupport.DbKodelisteImpl;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class TestDbKodelisteImpl extends DbKodelisteImpl implements TestDbKodeliste {
    @Override
    public TestDbKodelisteIdImpl getId() {
        return (TestDbKodelisteIdImpl) super.getId();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
