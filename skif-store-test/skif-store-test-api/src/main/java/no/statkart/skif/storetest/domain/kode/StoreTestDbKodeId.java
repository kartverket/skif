package no.statkart.skif.storetest.domain.kode;

import no.statkart.skif.store.kodelistesupport.DbKodeId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface StoreTestDbKodeId<T extends StoreTestDbKode> extends DbKodeId<T>, StoreTestKodeId<T> {
}
