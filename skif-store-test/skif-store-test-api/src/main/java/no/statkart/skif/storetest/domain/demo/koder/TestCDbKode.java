package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.kodelistesupport.DbSubclassedKode;
import no.statkart.skif.storetest.domain.kode.TestDbKode;
import no.statkart.skif.storetest.domain.kode.TestDbSubclassedKodeImpl;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class TestCDbKode extends TestDbSubclassedKodeImpl implements DbSubclassedKode, TestDbKode {
}
