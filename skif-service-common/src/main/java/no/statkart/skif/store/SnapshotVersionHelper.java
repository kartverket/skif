package no.statkart.skif.store;

import no.statkart.skif.exception.ConfigurationException;

import java.sql.Timestamp;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class SnapshotVersionHelper {
    public static Timestamp subtract(Timestamp timestamp, int nanos) {
        int n = timestamp.getNanos();
        long time = timestamp.getTime();
        if (n < nanos) {
            time -= 1000;
            n += 1000000000;
        }
        n -= nanos;
        Timestamp t = new Timestamp(time);
        t.setNanos(n);
        return t;
    }

    public static SnapshotVersion subtract(SnapshotVersion snapshotVersion, int nanos) {
        return SnapshotVersion.createInstance(subtract(snapshotVersion.getTimestamp(), nanos));
    }

    /**
     * Beregner minste {@code SnapshotVersion} før {@code snapshotVersion}. Laveste oppløsning i databasen
     * er 1000 nanos
     */
    public static SnapshotVersion calcJustBeforeOf(SnapshotVersion snapshotVersion) {
        return SnapshotVersion.createInstance(calcJustBeforeOf(snapshotVersion.getTimestamp()));
    }

    /**
     * Beregner minste {@code Timestamp} før {@code timestamp}. Laveste oppløsning i databasen
     * er 1000 nanos
     */
    public static Timestamp calcJustBeforeOf(Timestamp timestamp) {
        return subtract(timestamp, 1000);
    }
}
