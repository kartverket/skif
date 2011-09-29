package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.DbSubclassedKode;
import no.statkart.skif.storetest.domain.TestDbKode;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class TestCDbKode extends TestDbSubclassedKodeImpl implements DbSubclassedKode, TestDbKode {
}
