package no.statkart.skif.storetest.service.endringslogg;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Kontroll;
import no.statkart.skif.store.endringslogg.Endringer;
import no.statkart.skif.store.endringslogg.ReturnerBobler;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.EndringId;

import javax.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import java.util.Collection;

/**
 * EJB for {@link EndringsloggService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@RolesAllowed("Innsyn")
@SuppressWarnings("unused")

@Stateless(name = "EndringsloggServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EndringsloggServiceEJBBean extends EJBTimedService implements EndringsloggService {
    @Inject
    @EJBServiceChain
    EndringsloggService serviceChain;


    @Nullable
    @Override
    public EndringId<?> findSisteEndringId() {
        return serviceChain.findSisteEndringId();
    }

    @Override
    public Endringer<Endring<EndringId<?>, ?>, EndringId<?>> findEndringer(@Nullable EndringId<?> id, Class<? extends BubbleObject> bobleklasse, @Nullable String filter, ReturnerBobler returnerBobler, int maksAntall) {
        return serviceChain.findEndringer(id, bobleklasse, filter, returnerBobler, maksAntall);
    }

    @Override
    public <T extends BubbleObject> Kontroll calcEndringskontroll(@Nullable EndringId<?> id, Class<T> bobleklasse, @Nullable String filter, int antall) {
        return serviceChain.calcEndringskontroll(id, bobleklasse, filter, antall);
    }

    @Override
    public <T extends BubbleObject> Kontroll calcObjektkontrollForList(Collection<? extends BubbleId<?>> ids, Class<T> bobleklasse) {
        return serviceChain.calcObjektkontrollForList(ids, bobleklasse);
    }
}
