package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.KodelisteImpl;

/**
 * @author Henrik Fredholm
 */
public class TestKodelisteImpl extends KodelisteImpl implements TestKodeliste {
    @Override
    public TestKodelisteIdImpl<?> getId() {
        return (TestKodelisteIdImpl<?>) super.getId();
    }
}
