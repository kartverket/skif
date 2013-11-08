package no.statkart.skif.wsversioning.wsapi.v2.mapping;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.store.SnapshotVersion;

/**
 * Mapping-interface for WSVersioning-prosjektets V2-API.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public interface WSVersioningMapping extends Mapping {
    no.statkart.skif.wsversioning.wsapi.v2.domain.SnapshotVersion d2w(SnapshotVersion snapshotVersion);
    SnapshotVersion w2d(no.statkart.skif.wsversioning.wsapi.v2.domain.SnapshotVersion snapshotVersion);
}
