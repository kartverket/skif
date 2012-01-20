package no.statkart.skif.persistence5.jdbc;

import no.statkart.skif.persistence5.TransactionalResource;
import no.statkart.skif.store.SnapshotVersion;

import java.sql.Connection;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface ConnectionManager  extends TransactionalResource {
    ConnectionForSnapshotVersion getForSnapshotVersion(SnapshotVersion snapshotVersion);
}
