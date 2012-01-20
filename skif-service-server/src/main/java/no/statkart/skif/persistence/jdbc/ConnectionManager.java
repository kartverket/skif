package no.statkart.skif.persistence.jdbc;

import no.statkart.skif.persistence.TransactionalResource;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface ConnectionManager  extends TransactionalResource {
    ConnectionForSnapshotVersion getForSnapshotVersion(SnapshotVersion snapshotVersion);
}
