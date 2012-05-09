package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class PrioritetIkkeTinglystDokumentSidestiltMedId <T extends PrioritetIkkeTinglystDokumentSidestiltMed> extends PrioritetIkkeTinglystDokumentId<T>{
    public PrioritetIkkeTinglystDokumentSidestiltMedId(Long value) {
        super(value);
    }

    public PrioritetIkkeTinglystDokumentSidestiltMedId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
