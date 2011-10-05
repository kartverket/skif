package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.kodelistesupport.DbKodeId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface TestDbKodeId<T extends TestDbKode> extends DbKodeId<T>, TestKodeId<T> {
}
