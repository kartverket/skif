package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodeIdImpl;
import no.statkart.skif.store.kodelistesupport.EnumKodeSupport;
import no.statkart.skif.storetest.domain.kode.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class AEnumKodeId extends EnumKodeIdImpl<AEnumKode> implements StoreTestEnumKodeId<AEnumKode> {
    private static EnumKodeSupport<StoreTestEnumKodeliste, StoreTestEnumKodelisteId<StoreTestEnumKodeliste>> kodeSupport = new EnumKodeSupport<StoreTestEnumKodeliste, StoreTestEnumKodelisteId<StoreTestEnumKodeliste>>(AEnumKodeId.class,new StoreTestEnumKodelisteId(1), "TestAEnumKodeliste");

    public static StoreTestKodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static AEnumKodeId IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static AEnumKodeId KodeAId = define(1, "A", "Kode A");
    public static AEnumKodeId KodeBId = define(2, "B", "Kode B");

    protected AEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected EnumKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static AEnumKodeId define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.defineKode(AEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey).getId();
    }

    public static AEnumKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(AEnumKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
