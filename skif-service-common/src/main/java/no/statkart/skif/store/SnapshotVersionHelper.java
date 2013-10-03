package no.statkart.skif.store;

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

}
