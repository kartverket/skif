package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.kodelistesupport.KodelisteId;

/**
 * @author Henrik Fredholm
 */
public interface TestKodelisteId<T extends TestKodeliste> extends KodelisteId<T>, StoreTestBubbleId<T> {
}
