package no.statkart.skif.service.proxy;

import no.statkart.skif.store.SnapshotVersion;

import java.util.Objects;


/**
 * Hjelpestruktur for å returnere 2 verdier fra metoden
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class SnapshotVersionD2WResult {
    public final SnapshotVersion snapshotVersion;
    public final int lastArg;


    public SnapshotVersionD2WResult(int lastArg) {
        this.lastArg = lastArg;
        this.snapshotVersion = null;
    }

    public SnapshotVersionD2WResult(SnapshotVersion snapshotVersion, int lastArg) {
        this.snapshotVersion = Objects.requireNonNull(snapshotVersion, "snapshotVersion");
        this.lastArg = lastArg;
    }
}
