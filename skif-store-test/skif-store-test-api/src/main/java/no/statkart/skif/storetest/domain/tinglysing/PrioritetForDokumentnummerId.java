package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class PrioritetForDokumentnummerId<T extends PrioritetForDokumentnummer> extends RettsstiftelseId<T> {
    public PrioritetForDokumentnummerId(Long value) {
        super(value);
    }

    public PrioritetForDokumentnummerId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static PrioritetForDokumentnummerId<?> create(long value) {
        return new PrioritetForDokumentnummerId<PrioritetForDokumentnummer>(new Long(value));
    }
}