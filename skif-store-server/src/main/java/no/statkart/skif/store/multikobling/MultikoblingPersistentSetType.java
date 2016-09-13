package no.statkart.skif.store.multikobling;

import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.usertype.UserCollectionType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Hibernate type for {@link MultikoblingPersistentSet}
 */
public class MultikoblingPersistentSetType implements UserCollectionType {
    @Override
    public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister) throws HibernateException {
        return new MultikoblingPersistentSet(session);
    }

    @Override
    public PersistentCollection wrap(SessionImplementor session, Object collection) {
        return new MultikoblingPersistentSet(session, (Set)collection);
    }

    @Override
    public Iterator getElementsIterator(Object collection) {
        return ((Set)collection).iterator();
    }

    @Override
    public boolean contains(Object collection, Object entity) {
        return ((Set)collection).contains(entity);
    }

    @Override
    public Object indexOf(Object collection, Object entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object replaceElements(Object original, Object target, CollectionPersister persister, Object owner, Map copyCache, SessionImplementor session) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return anticipatedSize>0 ? new HashSet(anticipatedSize) : new HashSet();
    }
}
