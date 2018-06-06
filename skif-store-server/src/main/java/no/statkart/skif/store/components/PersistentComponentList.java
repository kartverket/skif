package no.statkart.skif.store.components;

import no.statkart.skif.store.ComponentList;
import org.hibernate.collection.PersistentList;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public class PersistentComponentList extends PersistentList implements ComponentList {
    public PersistentComponentList(SessionImplementor session) {
        super(session);
    }

    public PersistentComponentList(SessionImplementor session, ComponentList list) {
        super(session, list);
    }

    public PersistentComponentList() {
    }

    @Override
    public void setOwner(Object owner) {
        super.setOwner(owner);

        if (wasInitialized()) {
            ComponentList cl = (ComponentList) list;
            cl.setOwner(owner);
        }
    }

    @Override
    public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
        super.beforeInitialize(persister, anticipatedSize);

        ComponentList cl = (ComponentList) list;
        cl.setOwner(getOwner());
    }
}
