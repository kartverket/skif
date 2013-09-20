package no.statkart.skif.storetest.domain.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeId;

/**
 * Id for {@link HistoriskDbKode}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class HistoriskDbKodeId<T extends HistoriskDbKode> extends StoreTestDbKodeId<T> {
    private static final long serialVersionUID = 1L;

    public HistoriskDbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    @Override
    public KodelisteId<?> getKodelisteId() {
        throw new UnsupportedOperationException();
    }
}
