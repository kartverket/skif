package no.statkart.skif.wsversioning.service;

import no.statkart.skif.wsversioning.domain.VegId;

import java.util.Set;

/**
 * Kompatibilitetsservice for gate-veg-mapping.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public interface GateService {
    Set<VegId<?>> findAlleGater();
}
