package no.statkart.skif.store;

import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingSet;
import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Abstract implementasjon av {@link ComponentCollection} for wrapping av {@link java.util.Set}.
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
public abstract class AbstractBubbleIdIdSet<O extends AbstractBubbleObject, E extends BubbleId<?>> extends ForwardingSet<E> implements BubbleIdCollection<O, E> {
    private static final long serialVersionUID = 1L;
    protected final RelationName relationName;
    protected Set<E> delegate;


    protected AbstractBubbleIdIdSet(RelationName relationName, Set<E> delegate) {
        this.relationName = relationName;
        this.delegate = delegate;
    }

    public abstract O getOwner();

    @Override
    protected Set<E> delegate() {
        return delegate;
    }

    protected void setDelegate(Set<E> newDelegate) {
        delegate = newDelegate;
    }

    @Override
    public Iterator<E> iterator() {
        return new IteratorWrapper(super.iterator());
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return standardRemoveAll(collection);
    }

    @Override
    public boolean add(E element) {
        boolean added = super.add(element);
        if (added) {
            O owner = getOwner();
            if (owner != null) {
                owner.onChangeRelation(relationName, null, element);
            } else {
                // TODO: Handle component not connected to owner
            }
        }
        return added;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object object) {
        boolean removed = super.remove(object);
        if (removed) {
            O owner = getOwner();
            if (owner != null) {
                owner.onChangeRelation(relationName, (E) object, null);
            } else {
                // TODO: Handle component not connected to owner
            }
        }
        return removed;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean added = false;
        for (E e : collection) {
            added |= add(e);
        }
        return added;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return standardRetainAll(collection);
    }

    @Override
    public void clear() {
        O owner = getOwner();
        if (owner != null) {
            for (E e : delegate) {
                owner.onChangeRelation(relationName, e, null);
            }
        } else {
            // TODO: Handle component not connected to owner
        }
        super.clear();
    }

    private class IteratorWrapper extends ForwardingIterator<E> {
        private final Iterator<E> delegate;
        private E current = null;

        private IteratorWrapper(Iterator<E> delegate) {
            this.delegate = delegate;
        }

        @Override
        protected Iterator<E> delegate() {
            return delegate;
        }

        @Override
        public E next() {
            current = super.next();
            return current;
        }

        @Override
        public void remove() {
            O owner = getOwner();
            if (owner != null) {
                owner.onChangeRelation(relationName, current, null);
            } else {
                // TODO: Handle component not connected to owner
            }
            super.remove();
        }
    }
}
