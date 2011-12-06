package no.statkart.skif.store3.persistence;

import no.statkart.skif.guava.Preconditions;
import no.statkart.skif.store.SnapshotVersionSeed;

/**
 * @author Henrik Fredholm
 */
public class PersistenceDescriptorWrapper<S, W extends PersistenceDescriptor<?>> implements PersistenceDescriptor<S> {
    protected W wrapped;
    protected S object;

    public PersistenceDescriptorWrapper(W wrapped) {
        this.wrapped = wrapped;

    }

    public W getWrapped() {
        return wrapped;
    }

    @Override
    public int getIndex() {
        return wrapped.getIndex();
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public SnapshotVersionSeed getSeed() {
        return wrapped.getSeed();
    }

    @Override
    public S getObject() {
        return object;
    }

    public void setObject(S object) {
        Preconditions.checkArgument(object==null, "object er allerede satt");
        this.object = object;
    }

    public void setWrapped(W wrapped) {
        this.wrapped = wrapped;
    }
}
