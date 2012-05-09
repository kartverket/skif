package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class PrioritetIkkeTinglystDokumentVeketForId <T extends PrioritetIkkeTinglystDokumentVeketFor> extends PrioritetIkkeTinglystDokumentId<T>{
    public PrioritetIkkeTinglystDokumentVeketForId(Long value) {
        super(value);
    }

    public PrioritetIkkeTinglystDokumentVeketForId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
