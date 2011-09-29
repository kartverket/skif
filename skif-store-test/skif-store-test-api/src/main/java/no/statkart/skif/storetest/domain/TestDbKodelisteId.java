package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.kodelistesupport.DbKodelisteId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public interface TestDbKodelisteId<T extends TestDbKodeliste> extends DbKodelisteId<T>, TestKodelisteId<T> {
}
