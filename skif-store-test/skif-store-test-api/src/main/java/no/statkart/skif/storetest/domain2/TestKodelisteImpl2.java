package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.kodelistesupport2.KodelisteImpl2;

/**
 * @author Henrik Fredholm
 */
public class TestKodelisteImpl2 extends KodelisteImpl2 implements TestKodeliste2 {
    @Override
    public TestKodelisteIdImpl2<?> getId() {
        return (TestKodelisteIdImpl2<?>) super.getId();
    }
}
