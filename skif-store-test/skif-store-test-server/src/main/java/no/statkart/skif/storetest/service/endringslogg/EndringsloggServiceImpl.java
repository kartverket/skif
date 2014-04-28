package no.statkart.skif.storetest.service.endringslogg;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.endringslogg.EndringManagerConfiguration;

/**
 * Implementasjon av {@link no.statkart.skif.storetest.service.endringslogg.EndringsloggService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EndringsloggServiceImpl extends no.statkart.skif.store.service.EndringsloggServiceImpl<Endring<?,?>> implements EndringsloggService {

    @Inject
    public EndringsloggServiceImpl(Provider<SnapshotVersion> snapshotVersionProvider, EndringManagerConfiguration endringManagerConfiguration, Store store, Provider<SessionSelector> sessionSelectorProvider) {
        super(snapshotVersionProvider, endringManagerConfiguration, store, sessionSelectorProvider);
    }
}
