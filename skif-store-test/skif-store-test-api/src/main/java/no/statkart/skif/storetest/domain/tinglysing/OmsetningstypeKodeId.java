package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author rorchr
 */
public class OmsetningstypeKodeId extends StoreTestDbKodeId<OmsetningstypeKode> {

    private static final StoreTestDbKodeSupport<OmsetningstypeKodeId> kodeSupport = new StoreTestDbKodeSupport<OmsetningstypeKodeId>(OmsetningstypeKodeId.class, 3L);
    public static final StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();

    public static final OmsetningstypeKodeId Uoppgitt = define(1);
    public static final OmsetningstypeKodeId FrittSalg = define(2);
    public static final OmsetningstypeKodeId Gave = define(3);
    public static final OmsetningstypeKodeId Ekspropriasjon = define(4);
    public static final OmsetningstypeKodeId Tvangssalg = define(5);
    public static final OmsetningstypeKodeId Uskiftebevilling = define(6);
    public static final OmsetningstypeKodeId Skifteoppgjoer = define(7);
    public static final OmsetningstypeKodeId OpploestSamboerskap = define(8);
    public static final OmsetningstypeKodeId Annet = define(9);

    protected OmsetningstypeKodeId(Object value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

    protected static OmsetningstypeKodeId define(long idValue) {
        return kodeSupport.defineId(idValue);
    }
}
