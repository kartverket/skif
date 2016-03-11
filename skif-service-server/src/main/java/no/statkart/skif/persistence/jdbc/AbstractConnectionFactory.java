package no.statkart.skif.persistence.jdbc;

import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.store.SnapshotVersion;

import java.sql.Connection;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class AbstractConnectionFactory implements ConnectionFactory {
    private final boolean isSnapshotChangable;
    private final SnapshotVersion snapshotVersion;
    private final boolean setSnapshotOnSession;


    public AbstractConnectionFactory(boolean snapshotChangable, SnapshotVersion snapshotVersion, boolean setSnapshotOnSession) {
        isSnapshotChangable = snapshotChangable;
        this.snapshotVersion = snapshotVersion;
        this.setSnapshotOnSession = setSnapshotOnSession;
    }

    @Override
    public boolean isSnapshotChangable() {
        return isSnapshotChangable;
    }

    @Override
    public boolean accepts(SnapshotVersion snapshotVersion) {
        if (isSnapshotChangable && this.snapshotVersion == SnapshotVersion.OLD) {
            return snapshotVersion != SnapshotVersion.CURRENT;
        } else if (isSnapshotChangable && this.snapshotVersion == SnapshotVersion.CURRENT) {
            return snapshotVersion != SnapshotVersion.OLD;
        } else {
            return this.snapshotVersion.equals(snapshotVersion);
        }
    }


    @Override
    public void setSnapshotVersion(Connection connection, SnapshotVersion snapshotVersion) {
        if (setSnapshotOnSession) {
            throw new NotImplementedException("Setting of snapshotVersion is not implemented"); // TODO
//            connection.executeSQL("select snapshot_time.set_t(:timestamp) from dual").setTimestamp("timestamp", snapshotVersion.getTimestamp()).executeUpdate();
        }
    }
}
