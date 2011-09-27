package no.statkart.skif.store;


import java.io.Serializable;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public class SnapshotVersion implements Serializable {
    public final static SnapshotVersion CURRENT = new SnapshotVersion(1000);
    public final static SnapshotVersion OLD = new SnapshotVersion(999);
    public final static SnapshotVersion NOT_VERSIONED = new SnapshotVersion(998);
    final long timestamp;

    public static SnapshotVersion createInstance(long timestamp) {
        if (timestamp == CURRENT.timestamp) return CURRENT;
        if (timestamp == OLD.timestamp) return OLD;
        if (timestamp == NOT_VERSIONED.timestamp) return NOT_VERSIONED;
        return new SnapshotVersion(timestamp);
    }

    protected SnapshotVersion(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SnapshotVersion that = (SnapshotVersion) o;

        if (timestamp != that.timestamp) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }

    private Object readResolve() {
        return createInstance(timestamp);
    }

    @Override
    public String toString() {
        return "SnapshotVersion{" +
                "timestamp=" + timestamp +
                '}';
    }

    private String getTimestamp() {
        if (this==CURRENT) return "CURRENT";
        if (this==OLD) return "OLD";
        if (this==NOT_VERSIONED) return "NOT_VERSIONED";
        return Long.toString(timestamp);
    }
}
