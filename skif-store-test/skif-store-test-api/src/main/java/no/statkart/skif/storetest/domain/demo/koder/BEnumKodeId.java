package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodeIdImpl;
import no.statkart.skif.store.kodelistesupport.EnumKodeSupport;
import no.statkart.skif.store.kodelistesupport.KodelisteId;
import no.statkart.skif.storetest.domain.kode.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class BEnumKodeId extends EnumKodeIdImpl<BEnumKode> implements StoreTestEnumKodeId<BEnumKode> {
    private static EnumKodeSupport<StoreTestEnumKodeliste, StoreTestEnumKodelisteId<StoreTestEnumKodeliste>> kodeSupport = new EnumKodeSupport<StoreTestEnumKodeliste, StoreTestEnumKodelisteId<StoreTestEnumKodeliste>>(BEnumKodeId.class, new StoreTestEnumKodelisteId(2), "TestBEnumKodeliste");

    public static KodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static BEnumKodeId IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static BEnumKodeId KodeAId = define(1, "A", "Kode A");
    public static BEnumKodeId KodeBId = define(2, "B", "Kode B");
    public static BEnumKodeId KodeCId = define(1000000000L, "C", "Kode C");

    protected BEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected EnumKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static BEnumKodeId define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.defineKode(BEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey).getId();
    }

    public static BEnumKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(BEnumKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
