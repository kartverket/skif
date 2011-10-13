package no.statkart.skif.store;

import no.statkart.skif.inject.Holder;

/**
 * @author Henrik Fredholm
 */
public class SnapshotVersionSeed implements Holder<SnapshotVersion> {
    ThreadLocal<SnapshotVersion> threadLocal;

    public SnapshotVersionSeed(final SnapshotVersion initialValue) {
        threadLocal = new ThreadLocal<SnapshotVersion>() {
            @Override
            protected SnapshotVersion initialValue() {
                return initialValue;
            }
        };
    }

    @Override
    public SnapshotVersion get() {
        return threadLocal.get();
    }

    @Override
    public SnapshotVersion set(SnapshotVersion newInstance) {
        SnapshotVersion old = threadLocal.get();
        threadLocal.set(newInstance);
        return old;
    }
}
