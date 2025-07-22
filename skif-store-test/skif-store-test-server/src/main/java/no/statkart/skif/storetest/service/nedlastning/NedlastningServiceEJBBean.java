package no.statkart.skif.storetest.service.nedlastning;

import com.google.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Kontroll;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * EJB for {@link NedlastningService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@RolesAllowed("Innsyn")
@SuppressWarnings("unused")

@Stateless(name = "no.statkart.skif.storetest.service.nedlastning.NedlastningServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class NedlastningServiceEJBBean extends EJBTimedService implements NedlastningService {
    @Inject
    @EJBServiceChain
    NedlastningService serviceChain;

    @Override
    public <I extends BubbleId<? extends T>, T extends BubbleObject> List<I> findIdsEtterId(@Nullable BubbleId<? extends T> id, Class<T> bobleklasse, @Nullable String filter, int maksAntall) {
        return serviceChain.findIdsEtterId(id, bobleklasse, filter, maksAntall);
    }

    @Override
    public <T extends BubbleObject> List<T> findObjekterEtterId(@Nullable BubbleId<? extends T> id, Class<T> bobleklasse, @Nullable String filter, int maksAntall) {
        return serviceChain.findObjekterEtterId(id, bobleklasse, filter, maksAntall);
    }

    @Override
    public <T extends BubbleObject> Kontroll calcObjektkontrollForRange(@Nullable BubbleId<? extends T> fraId, @Nullable BubbleId<? extends T> tilId, Class<T> bobleklasse, @Nullable String filter) {
        return serviceChain.calcObjektkontrollForRange(fraId, tilId, bobleklasse, filter);
    }

    @Override
    public <I extends BubbleId<? extends T>, T extends BubbleObject> Kontroll calcObjektkontrollForList(Collection<I> ids, Class<T> bobleklasse) {
        return serviceChain.calcObjektkontrollForList(ids, bobleklasse);
    }
}
