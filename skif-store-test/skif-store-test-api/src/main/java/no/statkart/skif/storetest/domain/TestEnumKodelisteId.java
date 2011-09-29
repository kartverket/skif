package no.statkart.skif.storetest.domain;


import no.statkart.skif.store.kodelistesupport.EnumKodelisteId;

/**
 * @author Henrik Fredholm
 */
public interface TestEnumKodelisteId<T extends TestEnumKodeliste> extends EnumKodelisteId<T>, TestKodelisteId<T> {
}
