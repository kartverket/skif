package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteLongId;

/**
 * Id for {@link StoreTest2KodelisteLong}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class StoreTest2KodelisteLongId<T extends StoreTest2KodelisteLong> extends KodelisteLongId<T> implements StoreTest2KodelisteId<T> {
    public StoreTest2KodelisteLongId(Long value) {
        super(value);
    }

    public StoreTest2KodelisteLongId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
