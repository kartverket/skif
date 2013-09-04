package no.statkart.skif.wsversioning.service;

import no.statkart.skif.wsversioning.domain.VegId;

import java.util.Set;

/**
 * Service for {@link no.statkart.skif.wsversioning.domain.Veg}-relaterte ting.
 *
 * @author Tor Egil R. Strand
 * @author 2.4.0
 */
public interface VegService {
    Set<VegId<?>> findAlleVeger();
}
