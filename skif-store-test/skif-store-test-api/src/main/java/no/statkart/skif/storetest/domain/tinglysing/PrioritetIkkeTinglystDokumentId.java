package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class PrioritetIkkeTinglystDokumentId<T extends PrioritetIkkeTinglystDokument> extends PaategningForMatrikkelenheterId<T> {
    public PrioritetIkkeTinglystDokumentId(Long value) {
        super(value);
    }

    public PrioritetIkkeTinglystDokumentId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static PrioritetIkkeTinglystDokumentId<?> create(long value) {
        return new PrioritetIkkeTinglystDokumentId<PrioritetIkkeTinglystDokument>(new Long(value));
    }
}
