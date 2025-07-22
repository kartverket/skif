package no.statkart.skif.wsversioning.service;

import com.google.inject.Inject;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.wsversioning.config.WSVersioningEJBInterceptorJEE;
import no.statkart.skif.wsversioning.domain.VegId;

import java.util.Set;

/**
 * EJB for {@link VegService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Stateless(name = "no.statkart.skif.wsversioning.service.VegServiceEJBBean")
@Interceptors(WSVersioningEJBInterceptorJEE.class)
public class VegServiceEJBBean extends EJBTimedService implements VegService {
    @Inject
    @EJBServiceChain
    private VegService serviceChain;

    @Override
    public Set<VegId<?>> findAlleVeger() {
        return serviceChain.findAlleVeger();
    }
}
