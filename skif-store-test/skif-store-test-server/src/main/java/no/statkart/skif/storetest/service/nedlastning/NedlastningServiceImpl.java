package no.statkart.skif.storetest.service.nedlastning;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.endringslogg.EndringManagerConfiguration;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
@SuppressWarnings("unused")
public class NedlastningServiceImpl extends no.statkart.skif.store.service.NedlastningServiceImpl implements NedlastningService {

    @Inject
    public NedlastningServiceImpl(Provider<SnapshotVersion> snapshotVersionProvider, Provider<SessionSelector> sessionSelectorProvider, EndringManagerConfiguration endringManagerConfiguration, Store store) {
        super(snapshotVersionProvider, sessionSelectorProvider, endringManagerConfiguration, store);
    }
}
