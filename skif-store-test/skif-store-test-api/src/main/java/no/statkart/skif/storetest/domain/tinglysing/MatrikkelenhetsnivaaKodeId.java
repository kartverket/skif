package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author rorchr
 */
public class MatrikkelenhetsnivaaKodeId extends StoreTestDbKodeId<MatrikkelenhetsnivaaKode> {
    
    private final static StoreTestDbKodeSupport<MatrikkelenhetsnivaaKodeId> kodeSupport = new StoreTestDbKodeSupport<MatrikkelenhetsnivaaKodeId>(MatrikkelenhetsnivaaKodeId.class, 4L);
    public final static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    
    public static final MatrikkelenhetsnivaaKodeId Grunn = define(1);
    public static final MatrikkelenhetsnivaaKodeId Feste = define(2);
    public static final MatrikkelenhetsnivaaKodeId Framfeste1 = define(3);
    public static final MatrikkelenhetsnivaaKodeId Framfeste2 = define(4);
    public static final MatrikkelenhetsnivaaKodeId Framfeste3 = define(5);
    public static final MatrikkelenhetsnivaaKodeId Framfeste4 = define(6);
    public static final MatrikkelenhetsnivaaKodeId Framfeste5 = define(7);
    public static final MatrikkelenhetsnivaaKodeId Framfeste6 = define(8);
    public static final MatrikkelenhetsnivaaKodeId Framfeste7 = define(9);
    public static final MatrikkelenhetsnivaaKodeId Framfeste8 = define(10);
    public static final MatrikkelenhetsnivaaKodeId Framfeste9 = define(11);

    public MatrikkelenhetsnivaaKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

    protected static MatrikkelenhetsnivaaKodeId define(long idValue) {
        return kodeSupport.defineId(idValue);
    }

}
