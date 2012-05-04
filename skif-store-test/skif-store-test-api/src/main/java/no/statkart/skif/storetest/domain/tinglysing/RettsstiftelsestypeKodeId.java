package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author rorchr
 */
public class RettsstiftelsestypeKodeId extends StoreTestDbKodeId<RettsstiftelsestypeKode> {

    private static final StoreTestDbKodeSupport<RettsstiftelsestypeKodeId> kodeSupport = new StoreTestDbKodeSupport<RettsstiftelsestypeKodeId>(RettsstiftelsestypeKodeId.class, 1L);
    public static final StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    
    public static final RettsstiftelsestypeKodeId HJ_HJG = define(1); // TODO: finn id etter at loadTestdata.sql er laget
    public static final RettsstiftelsestypeKodeId FE_FES = define(2); // TODO: finn id etter at loadTestdata.sql er laget
    public static final RettsstiftelsestypeKodeId JS_JSA = define(3);  // TODO: finn id etter at loadTestdata.sql er laget
    public static final RettsstiftelsestypeKodeId PA_PAF = define(100); // TODO: finn id etter at loadTestdata.sql er laget

    public RettsstiftelsestypeKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public Long getValue() {
        return (Long) super.getValue();
    }

    protected static RettsstiftelsestypeKodeId define(long idValue) {
        return kodeSupport.defineId(idValue);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }
}

