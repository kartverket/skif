package no.statkart.skif.storetest.domain2.kodeliste;

import no.statkart.skif.store2.kodelistesupport2.DbKodeImpl2;
import no.statkart.skif.store2.kodelistesupport2.DbSubclassedKode2;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbSubclassedKode;
import no.statkart.skif.storetest.domain2.TestDbKode2;


/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class TestCDbKode2 extends TestDbSubclassedKodeImpl2 implements DbSubclassedKode2, TestDbKode2 {
}
