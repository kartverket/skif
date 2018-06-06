package no.statkart.skif.store.components;

import no.statkart.skif.store.Components;
import no.statkart.skif.store.ComponentList;
import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;
import org.hibernate.usertype.UserCollectionType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ComponentListType implements UserCollectionType {
    @Override
    public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister) throws HibernateException {
        return new PersistentComponentList(session);
    }

    @Override
    public PersistentCollection wrap(SessionImplementor session, Object collection) {
        return new PersistentComponentList(session, (ComponentList) collection);
    }

    @Override
    public Iterator getElementsIterator(Object collection) {
        return ((ComponentList) collection).iterator();
    }

    @Override
    public boolean contains(Object collection, Object entity) {
        return ((ComponentList) collection).contains(entity);
    }

    @Override
    public Object indexOf(Object collection, Object entity) {
        return ((ComponentList) collection).indexOf(entity);
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
        return Components.newList(anticipatedSize <= 0 ? new ArrayList() : new ArrayList(anticipatedSize));
    }
}
