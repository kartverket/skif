package no.statkart.skif.store.kodeliste;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.kodeliste.KodelistePersistenceSessionSubtypeHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class KodelisteServiceImpl implements KodelisteService {
    @Inject
    private PersistenceSessionManager persistenceSessionManager;
    @Inject
    private Store store;

    protected KodelistePersistenceSessionSubtypeHandler getKodelisteSubtypeHandler(SnapshotVersion snapshotVersion) {
        PersistenceSessionForSnapshot forSnapshotVersion = persistenceSessionManager.getForSnapshotVersion(snapshotVersion);
        return forSnapshotVersion.getImplementation(KodelistePersistenceSessionSubtypeHandler.class);

    }

    @Override
    public KodelisteTransfer<? extends KodelisteId<?>> getKodelister(SnapshotVersion snapshotVersion) {
        List<KodelisteId<?>> kodelisteIds = getKodelisteSubtypeHandler(snapshotVersion).getKodelisteIds();
        List<Kodeliste> kodelisteList =store.get(kodelisteIds);
        List<KodeId<?>> kodeIds = new ArrayList<KodeId<?>>(kodelisteList.size() * 10);
        for (Kodeliste kodeliste : kodelisteList) {
            kodeIds.addAll(kodeliste.getKoderIds());
        }
        List<Kode> koder = store.get(kodeIds);
        KodelisteTransfer<KodelisteId<?>> kodelisteTransfer = new KodelisteTransfer<KodelisteId<?>>(kodelisteIds, Iterables.concat(kodelisteList, koder));
        return kodelisteTransfer;
    }

    @Override
    public KodelisteTransfer<? extends KodelisteId<?>> getKodeliste(String kodeIdClassName, SnapshotVersion snapshotVersion) {
        throw new NotImplementedException();
    }
}
