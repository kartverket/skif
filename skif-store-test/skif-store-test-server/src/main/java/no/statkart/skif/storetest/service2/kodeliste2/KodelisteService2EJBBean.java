package no.statkart.skif.storetest.service2.kodeliste2;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store2.KodelisteTransfer2;
import no.statkart.skif.store2.kodelistesupport2.KodelisteId2;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
@Stateless(name = "no.statkart.skif.storetest.service.kodeliste.KodelisteServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class KodelisteService2EJBBean extends EJBTimedService implements KodelisteService2 {
    @Inject  @EJBServiceChain
    private KodelisteService2 serviceImpl;

    @Override
    public Collection<? extends KodelisteId2> getKodelisteIds() {
        return serviceImpl.getKodelisteIds();

    }

    @Override
    public KodelisteTransfer2 getKodelister() {
        return serviceImpl.getKodelister();
    }
}