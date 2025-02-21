package no.statkart.skif.store;

import java.sql.Timestamp;

/**
 * @since 2.1
 */
public class SnapshotVersionHelper {
    public static Timestamp subtract(Timestamp timestamp, int nanos) {
        return adjust(timestamp, -nanos);
    }

    /**
     * Endrer verdien med <code>nanos</code> nanosekunder. Støtter foreløpig ikke store endringer på mer enn ett sekund.
     *
     * @since 2.4.4
     */
    public static Timestamp adjust(Timestamp timestamp, int nanos) {
        int n = timestamp.getNanos();
        long time = timestamp.getTime();

        n += nanos;

        if (n < 0) {
            time -= 1000;
            n += 1000000000;
        }

        if (n >= 1000000000) {
            time += 1000;
            n -= 1000000000;
        }

        Timestamp t = new Timestamp(time);
        t.setNanos(n);

        return t;
    }

    public static SnapshotVersion subtract(SnapshotVersion snapshotVersion, int nanos) {
        return SnapshotVersion.createInstance(subtract(snapshotVersion.getTimestamp(), nanos));
    }

}
