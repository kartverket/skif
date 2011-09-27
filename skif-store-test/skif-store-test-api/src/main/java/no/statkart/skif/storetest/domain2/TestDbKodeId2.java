package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.kodelistesupport2.DbKodeId2;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public interface TestDbKodeId2<T extends TestDbKode2> extends DbKodeId2<T>, TestKodeId2<T> {
}
