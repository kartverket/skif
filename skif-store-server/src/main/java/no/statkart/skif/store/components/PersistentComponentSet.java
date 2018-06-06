package no.statkart.skif.store.components;

import no.statkart.skif.store.ComponentSet;
import no.statkart.skif.store.ComponentWithOwnerReference;
import org.hibernate.collection.PersistentSet;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public class PersistentComponentSet extends PersistentSet implements ComponentSet {
    public PersistentComponentSet(SessionImplementor session) {
        super(session);
    }

    public PersistentComponentSet(SessionImplementor session, ComponentSet set) {
        super(session, set);
    }

    public PersistentComponentSet() {
    }

    @Override
    public void setOwner(Object owner) {
        super.setOwner(owner);

        if (wasInitialized()) {
            ComponentSet cl = (ComponentSet) set;
            cl.setOwner(owner);
        }
    }

    @Override
    public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
        super.beforeInitialize(persister, anticipatedSize);

        ComponentSet cl = (ComponentSet) set;
        cl.setOwner(getOwner());
    }
}
