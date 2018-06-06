package no.statkart.skif.store.components;

import no.statkart.skif.store.ComponentSet;
import no.statkart.skif.store.Components;
import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;
import org.hibernate.usertype.UserCollectionType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class ComponentSetType implements UserCollectionType {
    @Override
    public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister) throws HibernateException {
        return new PersistentComponentSet(session);
    }

    @Override
    public PersistentCollection wrap(SessionImplementor session, Object collection) {
        return new PersistentComponentSet(session, (ComponentSet) collection);
    }

    @Override
    public Iterator getElementsIterator(Object collection) {
        return ((ComponentSet) collection).iterator();
    }

    @Override
    public boolean contains(Object collection, Object entity) {
        return ((ComponentSet) collection).contains(entity);
    }

    @Override
    public Object indexOf(Object collection, Object entity) {
        throw new UnsupportedOperationException("No indexOf on Set");
    }

    @Override
    public Object replaceElements(Object original, Object target, CollectionPersister persister, Object owner, Map copyCache, SessionImplementor session) throws HibernateException {
        // Basert på CollectionType. Se kommentarene der.

        java.util.Collection result = ( java.util.Collection ) target;
        result.clear();

        Type elemType = persister.getElementType();
        Iterator iter = ( (java.util.Collection) original ).iterator();
        while ( iter.hasNext() ) {
            result.add( elemType.replace( iter.next(), null, session, owner, copyCache ) );
        }

        if ( original instanceof PersistentCollection ) {
            if ( result instanceof PersistentCollection ) {
                if ( ! ( ( PersistentCollection ) original ).isDirty() ) {
                    ( ( PersistentCollection ) result ).clearDirty();
                }
            }
        }

        return result;
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return Components.newSet(anticipatedSize <= 0 ? new HashSet() : new HashSet(anticipatedSize));
    }
}
