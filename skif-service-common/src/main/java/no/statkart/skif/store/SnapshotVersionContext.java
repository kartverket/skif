package no.statkart.skif.store;

import no.statkart.skif.inject.Holder;

/**
 * Holder på default[@code SnapshotVersion} for kjørende tråd og brukes bl.a av mapping rammeverket for mappe
 * {@code BubbleId} i wsapi (som ikke har {@code SnapshotVersion}) til BubbleId i javaapi (som har {@code SnapshotVersion}).
 *
 * <P/>{@code SnapshotVersionContext} overføres automatisk fra klient til server for alle servicekall.
 * Det er mulig å få tak i {@code SnapshotVersionContext} og inneværende {@code SnapshotVersion} for kjørende tråd
 * via Guice injection, men det er også mulg å få tak {@code SnapshotVersionContext} direkte via
 * {@link  SnapshotVersionContext#getInstance()}.
 *
 * {@code  SnapshotVersionContext.getInstance().setSnap}
 */
public class SnapshotVersionContext {
    private static final SnapshotVersionContext INSTANCE;

    static {
        INSTANCE = new SnapshotVersionContext();
    }

    private final ThreadLocal<SnapshotVersion> snapshotVersionThreadLocal;

    private SnapshotVersionContext() {
        this.snapshotVersionThreadLocal = new ThreadLocal<SnapshotVersion>() {
            @Override
            protected SnapshotVersion initialValue() {
                return SnapshotVersion.CURRENT;
            }
        };
    }

    public static SnapshotVersionContext getInstance() {
        return INSTANCE;
    }

    public SnapshotVersion getSnapshotVersion() {
        return snapshotVersionThreadLocal.get();
    }

    public SnapshotVersion setSnapshotVersion(SnapshotVersion snapshotVersion) {
        SnapshotVersion old = getSnapshotVersion();
        snapshotVersionThreadLocal.set(snapshotVersion);
        return old;
    }

}
