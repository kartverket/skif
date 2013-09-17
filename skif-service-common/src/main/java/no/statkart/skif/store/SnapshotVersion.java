package no.statkart.skif.store;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Henrik Fredholm
 */
public class SnapshotVersion implements Serializable, Comparable<SnapshotVersion> {
    private static final long serialVersionUID = 1L;

    public final static SnapshotVersion START = new SnapshotVersion("2000-01-01 00:00:00.0");
    public final static SnapshotVersion CURRENT = new SnapshotVersion("9999-01-01 00:00:00.0");
    public final static SnapshotVersion OLD = new SnapshotVersion("9997-01-01 00:00:00.0");

    private final long time;
    private final int nanos;

    public static SnapshotVersion createInstance(String timestampString) {
        return createInstance(Timestamp.valueOf(timestampString));
    }

    public static SnapshotVersion createInstance(Timestamp timestamp) {
        if (CURRENT.equalsTimestamp(timestamp)) return CURRENT;
        if (OLD.equalsTimestamp(timestamp)) return OLD;

        return new SnapshotVersion(timestamp);
    }

    protected SnapshotVersion(String timestampString) {
        this(Timestamp.valueOf(timestampString));
    }

    protected SnapshotVersion(Timestamp timestamp) {
        time = timestamp.getTime();
        nanos = timestamp.getNanos();
    }

    public boolean equalsTimestamp(Timestamp o) {
        if (o == null) return false;
        if (time != o.getTime()) return false;
        if (nanos != o.getNanos()) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SnapshotVersion that = (SnapshotVersion) o;

        if (nanos != that.nanos) return false;
        if (time != that.time) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (time ^ (time >>> 32));
        result = 31 * result + nanos;
        return result;
    }

    private Object readResolve() {
        Timestamp timestamp = getTimestamp();
        return createInstance(timestamp);
    }


    @Override
    public String toString() {
        return "SnapshotVersion{" +
                "timestamp=" + getTimestampString() +
                '}';
    }

    public Timestamp getTimestamp() {
        Timestamp timestamp = new Timestamp(time);
        timestamp.setNanos(nanos);
        return timestamp;
    }

    public String getTimestampString() {
        if (this == CURRENT) return "CURRENT";
        if (this == OLD) return "OLD";
        return getTimestamp().toString();
    }

    @Override
    public int compareTo(SnapshotVersion other) {
        if (this.time < other.time) {
            return -1;
        } else if (this.time > other.time) {
            return 1;
        } else {
            if (this.nanos < other.nanos) {
                return -1;
            } else if (this.nanos > other.nanos) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public boolean between(Timestamp oppdateringsdato, Timestamp sluttdato) {
        return this.between(SnapshotVersion.createInstance(oppdateringsdato), SnapshotVersion.createInstance(sluttdato));
    }

    public boolean between(SnapshotVersion oppdateringsdato, SnapshotVersion sluttdato) {
        return this.compareTo(oppdateringsdato) >= 0 &&
                (this.compareTo(sluttdato) == -1 || (this.equals(SnapshotVersion.CURRENT) && sluttdato.equals(SnapshotVersion.CURRENT)));
    }
}
