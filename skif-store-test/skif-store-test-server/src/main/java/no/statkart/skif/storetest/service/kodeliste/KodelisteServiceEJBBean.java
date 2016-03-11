package no.statkart.skif.storetest.service.kodeliste;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * @author Henrik Fredholm
 */
@SuppressWarnings("unused")

@Stateless(name = "no.statkart.skif.storetest.service.kodeliste.KodelisteServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class KodelisteServiceEJBBean extends EJBTimedService implements KodelisteService {
    @Inject  @EJBServiceChain
    private KodelisteService serviceImpl;

    @Override
    public KodelisteTransfer<? extends KodelisteId<?>> getKodeliste(String kodeIdClassName, SnapshotVersion snapshotVersion) {
        return serviceImpl.getKodeliste(kodeIdClassName, snapshotVersion);
    }

    @Override
    public KodelisteTransfer<? extends KodelisteId<?>> getKodelister(SnapshotVersion snapshotVersion) {
        return serviceImpl.getKodelister(snapshotVersion);
    }
}

