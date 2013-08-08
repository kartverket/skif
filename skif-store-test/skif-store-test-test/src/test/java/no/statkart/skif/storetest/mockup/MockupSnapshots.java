package no.statkart.skif.storetest.mockup;

import no.statkart.skif.store.SnapshotVersion;

/**
 * Hjelpeklasse for snapshots som anvendes i mockups
 * @author Henrik Fredholm
 * @since 2.3
 */
public class MockupSnapshots {
    public static SnapshotVersion S0 = SnapshotVersion.createInstance("2011-10-02 08:00:00.00");
    public static SnapshotVersion S1 = SnapshotVersion.createInstance("2011-10-02 08:01:00.00");
    public static SnapshotVersion S2 = SnapshotVersion.createInstance("2011-10-02 08:02:00.00");
    public static SnapshotVersion S2_01 = SnapshotVersion.createInstance("2011-10-02 08:02:01.00");
    public static SnapshotVersion S2_02 = SnapshotVersion.createInstance("2011-10-02 08:02:02.00");
    public static SnapshotVersion S2_03 = SnapshotVersion.createInstance("2011-10-02 08:02:03.00");
    public static SnapshotVersion S3 = SnapshotVersion.createInstance("2011-10-02 08:03:00.00");
    public static SnapshotVersion S3_30 = SnapshotVersion.createInstance("2011-10-02 08:03:30.00");
    public static SnapshotVersion S4 = SnapshotVersion.createInstance("2011-10-02 08:04:00.00");
    public static SnapshotVersion S4_justafter = SnapshotVersion.createInstance("2011-10-02 08:04:00.01");

    public static SnapshotVersion CURRENT = SnapshotVersion.CURRENT;
    public static SnapshotVersion OLD = SnapshotVersion.OLD;
}
