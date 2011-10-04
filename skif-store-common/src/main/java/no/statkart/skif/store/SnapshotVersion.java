package no.statkart.skif.store;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public class SnapshotVersion implements Serializable {
    public final static SnapshotVersion CURRENT = new SnapshotVersion("9999-01-01 00:00:00.0");
    public final static SnapshotVersion OLD = new SnapshotVersion("9998-01-01 00:00:00.0");
    public final static SnapshotVersion NOT_VERSIONED = new SnapshotVersion("9997-01-01 00:00:00.0");
    final String timestamp;

    public static SnapshotVersion createInstance(String timestamp) {
        if (timestamp.equals(CURRENT.timestamp) ) return CURRENT;
        if (timestamp.equals(OLD.timestamp)) return OLD;
        if (timestamp.equals(NOT_VERSIONED.timestamp)) return NOT_VERSIONED;

        return new SnapshotVersion(timestamp);
    }

    protected SnapshotVersion(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SnapshotVersion that = (SnapshotVersion) o;

        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return timestamp != null ? timestamp.hashCode() : 0;
    }

    private Object readResolve() {
        return createInstance(timestamp);
    }

    @Override
    public String toString() {
        return "SnapshotVersion{" +
                "timestamp=" + getTimestampString() +
                '}';
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTimestampString() {
        if (this == CURRENT) return "CURRENT";
        if (this == OLD) return "OLD";
        if (this == NOT_VERSIONED) return "NOT_VERSIONED";
        return timestamp;
    }
}
