package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;

public class SnapshotVersionType {
    /* Controls the value of {@link #snapshotVersionSeed} for newly created BubbleIdTypes (is a Seed of Seeds) */
    private static SnapshotVersionSeed snapshotVersionSeedSeed = new SnapshotVersionSeed(SnapshotVersion.CURRENT);

    /* Holds the SnapshotVersion that will be assigned to BubbleIds materialized by this instance */
    private SnapshotVersionSeed snapshotVersionSeed = snapshotVersionSeedSeed;

    @SuppressWarnings("WeakerAccess")
    protected final SnapshotVersion getSnapshotVersion() {
       return snapshotVersionSeed.get();
    }

//    protected SnapshotVersionSeed getSnapshotVersionSeed() {
//        return snapshotVersionSeed;
//    }

// Trenger ikke disse:
//    public void setSnapshotVersionSeed(SnapshotVersionSeed snapshotVersionSeed) {
//        this.snapshotVersionSeed = snapshotVersionSeed;
//    }
//
//    public static SnapshotVersionSeed getSnapshotVersionSeedSeed() {
//        return snapshotVersionSeedSeed;
//    }

    public static void setSnapshotVersionSeedSeed(SnapshotVersionSeed newSnapshotVersionSeedSeed) {
        snapshotVersionSeedSeed = newSnapshotVersionSeedSeed;
    }

}
