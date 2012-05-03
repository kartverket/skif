package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author rorchr
 */
public class RettsstiftelsestypeKodeId extends StoreTestDbKodeId<RettsstiftelsestypeKode> {
    private static StoreTestDbKodeSupport<RettsstiftelsestypeKodeId> kodeSupport = new StoreTestDbKodeSupport<RettsstiftelsestypeKodeId>(RettsstiftelsestypeKodeId.class, 1L);

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static RettsstiftelsestypeKodeId PAF = define(157); // Databasen må populeres med rettsstiftelsestyper, så må vi sjekke hvilken id PAF har i databasen og sette den inn her

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public RettsstiftelsestypeKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    protected static RettsstiftelsestypeKodeId define(long idValue) {
        return kodeSupport.defineId(idValue);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

    /**
     * Typesikker sammenlikning av kodeId som ikke tar hensyn til {@code SnapshotVersion}
     */
    public boolean equalTo(RettsstiftelsestypeKodeId id) {
        return this.equalsIgnoreSnapshotVersion(id);
    }

}

