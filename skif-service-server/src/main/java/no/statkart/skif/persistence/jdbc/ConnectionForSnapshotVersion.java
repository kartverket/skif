package no.statkart.skif.persistence.jdbc;

import java.sql.Connection;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface ConnectionForSnapshotVersion extends Connection, ConnectionReservationForSnapshot {
}
