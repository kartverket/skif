package no.statkart.skif.store;

/**
 * Holder på default {@link SnapshotVersion#CURRENT SnapshotVersion} for kjørende tråd og brukes bl.a av mapping rammeverket for mappe
 * {@code BubbleId} i wsapi (som ikke har {@code SnapshotVersion}) til BubbleId i java-api (som har {@code SnapshotVersion}).
 *
 * <p>{@code SnapshotVersionContext} overføres automatisk fra klient til server for alle servicekall.
 * Det er mulig å få tak i {@code SnapshotVersionContext} og inneværende {@code SnapshotVersion} for kjørende tråd
 * via Guice injection, men det er også mulig å få tak {@code SnapshotVersionContext} direkte via
 * {@link SnapshotVersionContext#getInstance()}.
 *
 * <br><br>
 * F.eks: <pre>{@code
 *  // Snapshot version for arbeidstråd
 *  SnapshotVersion snapshotVersion = SnapshotVersionContext.getInstance().getSnapshotVersion();
 *  ...
 *  // Angir ny snapshot version for arbeidstråd
 *  SnapshotVersionContext.getInstance().setSnapshotVersion(snapshotVersion);
 * }</pre>
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

    /**
     * @return snapshot version for current thread or {@link SnapshotVersion#CURRENT default value} when not configured.
     */
    public SnapshotVersion getSnapshotVersion() {
        return snapshotVersionThreadLocal.get();
    }

    public SnapshotVersion setSnapshotVersion(SnapshotVersion snapshotVersion) {
        SnapshotVersion old = getSnapshotVersion();
        snapshotVersionThreadLocal.set(snapshotVersion);
        return old;
    }

}
