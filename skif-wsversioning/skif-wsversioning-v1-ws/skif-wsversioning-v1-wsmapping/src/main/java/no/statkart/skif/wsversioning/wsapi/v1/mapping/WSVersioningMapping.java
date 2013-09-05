package no.statkart.skif.wsversioning.wsapi.v1.mapping;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.wsversioning.domain.VegId;

/**
 * Mapping-interface for WSVersioning-prosjektets v1-API.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public interface WSVersioningMapping extends Mapping {
    no.statkart.skif.wsversioning.wsapi.v1.domain.SnapshotVersion d2w(SnapshotVersion snapshotVersion);
    SnapshotVersion w2d(no.statkart.skif.wsversioning.wsapi.v1.domain.SnapshotVersion snapshotVersion);

    no.statkart.skif.wsversioning.wsapi.v1.domain.GateId d2w(VegId<?> vegId);
    VegId<?> w2d(no.statkart.skif.wsversioning.wsapi.v1.domain.GateId gateId);
}
