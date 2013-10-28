package no.statkart.skif.storetest.service.endringslogg;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.Kontroll;

import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.List;

/**
 * EJB for {@link EndringsloggService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@RolesAllowed("Innsyn")
@Stateless(name = "EndringsloggServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EndringsloggServiceEJBBean extends EJBTimedService implements EndringsloggService {
    @Inject
    @EJBServiceChain
    EndringsloggService serviceChain;

    @Override
    public long findSisteEndringsnummer(SnapshotVersion snapshotVersion) {
        return serviceChain.findSisteEndringsnummer(snapshotVersion);
    }

    @Override
    public <E extends Endring> List<E> findEndringerEtterEndringsnummer(long endringsnummer, Class<E> endringsklasse ,  int maksAntall, SnapshotVersion snapshotVersion) {
        return serviceChain.findEndringerEtterEndringsnummer(endringsnummer, endringsklasse, maksAntall, snapshotVersion);
    }

    @Override
    public <I extends BubbleId<? extends T>, T extends BubbleObject> List<I> findIdsEtterId(@Nullable BubbleId<? extends T> id, Class<T> klassefilter, int maxAntall, SnapshotVersion snapshotVersion) {
        return serviceChain.findIdsEtterId(id, klassefilter, maxAntall, snapshotVersion);
    }

    @Override
    public <K extends Kontroll, I extends StoreTestBubbleId<T>, T extends StoreTestBubble> K calcKontrollForRange(@Nullable BubbleId<? extends T> fraId, @Nullable BubbleId<? extends T> tilId,  Class<T> klassefilter, SnapshotVersion snapshotVersion) {
        return serviceChain.calcKontrollForRange(fraId, tilId, klassefilter, snapshotVersion);
    }

    @Override
    public <K extends Kontroll, I extends StoreTestBubbleId<? extends T>, T extends StoreTestBubble> K calcKontrollForList(Collection<I> ids,  Class<T> klassefilter,  SnapshotVersion snapshotVersion) {
        return serviceChain.calcKontrollForList(ids, klassefilter, snapshotVersion);
    }

}
