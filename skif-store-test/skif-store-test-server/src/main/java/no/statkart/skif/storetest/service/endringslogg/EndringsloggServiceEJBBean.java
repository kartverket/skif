package no.statkart.skif.storetest.service.endringslogg;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.domain.endringslogg.Endring;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
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
    public long findSisteEndringsnummer() {
        return serviceChain.findSisteEndringsnummer();
    }

    @Override
    public List<Endring> findEndringerEtterEndringsnummer(long endringsnummer, int maksAntall) {
        return serviceChain.findEndringerEtterEndringsnummer(endringsnummer, maksAntall);
    }
}
