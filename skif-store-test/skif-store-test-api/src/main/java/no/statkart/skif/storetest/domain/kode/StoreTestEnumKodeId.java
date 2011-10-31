package no.statkart.skif.storetest.domain.kode;

import no.statkart.skif.store.kodelistesupport.EnumKodeId;

/**
 * @author Henrik Fredholm
 */
public interface StoreTestEnumKodeId<T extends StoreTestEnumKode> extends EnumKodeId<T>, StoreTestKodeId<T> {
}
