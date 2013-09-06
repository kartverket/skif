package no.statkart.skif.wsversioning.service;

import com.google.inject.Inject;
import no.statkart.skif.wsversioning.domain.VegId;

import java.util.Set;

/**
 * Implementasjon av {@link GateService}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class GateServiceImpl implements GateService {
    @Inject
    private VegService vegService;

    @Override
    public Set<VegId<?>> findAlleGater() {
        return vegService.findAlleVeger();
    }
}
