package no.statkart.skif.storetest.persistence;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 */
public class SnapshotVersions {
    public static String T1 = "2011-10-02 08:01:00.00";
    public static String T2 = "2011-10-02 08:02:00.00";
    public static String T3 = "2011-10-02 08:03:00.00";
    public static String T4 = "2011-10-02 08:04:00.00";
    public static SnapshotVersion CURRENT = SnapshotVersion.CURRENT;
    public static SnapshotVersion OLD = SnapshotVersion.OLD;
    public static SnapshotVersion S1 = SnapshotVersion.createInstance(T1);
    public static SnapshotVersion S2 = SnapshotVersion.createInstance(T2);
    public static SnapshotVersion S3 = SnapshotVersion.createInstance(T3);
    public static SnapshotVersion S4 = SnapshotVersion.createInstance(T4);
}
