package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.kodelistesupport.EnumKodeId;

/**
 * @author Henrik Fredholm
 */
public interface TestEnumKodeId<T extends TestEnumKode> extends EnumKodeId<T>, TestKodeId<T> {
}
