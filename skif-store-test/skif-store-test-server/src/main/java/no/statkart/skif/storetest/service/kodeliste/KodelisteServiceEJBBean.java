package no.statkart.skif.storetest.service.kodeliste;

import com.google.inject.Inject;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.KodelisteTransfer;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.service.store.StoreServiceEJBBean;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
@Stateless(name = "no.statkart.skif.storetest.service.kodeliste.KodelisteServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class KodelisteServiceEJBBean extends EJBTimedService implements KodelisteService {
    @Inject  @EJBServiceChain
    private KodelisteService serviceImpl;

    @Override
    public Collection<? extends KodelisteId> getKodelisteIds() {
        return serviceImpl.getKodelisteIds();

    }

    @Override
    public KodelisteTransfer getKodelister() {
        return serviceImpl.getKodelister();
    }
}