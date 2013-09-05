package no.statkart.skif.wsversioning.service;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.wsversioning.config.WSVersioningEJBInterceptorJEE;
import no.statkart.skif.wsversioning.domain.VegId;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Set;

/**
 * EJB for {@link GateService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Stateless(name = "no.statkart.skif.wsversioning.service.GateServiceEJBBean")
@Interceptors(WSVersioningEJBInterceptorJEE.class)
public class GateServiceEJBBean extends EJBTimedService implements GateService {
    @Inject
    @EJBServiceChain
    private GateService serviceChain;

    @Override
    public Set<VegId<?>> findAlleGater() {
        return serviceChain.findAlleGater();
    }
}
