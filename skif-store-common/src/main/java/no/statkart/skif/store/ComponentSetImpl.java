package no.statkart.skif.store;

import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Abstract implementasjon av {@link no.statkart.skif.store.ComponentCollection} for wrapping av {@link java.util.Set}.
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
public class ComponentSetImpl<O, E extends ComponentWithOwnerReference<? super O>> extends ForwardingSet<E> implements ComponentSet<O, E> {
    private static final long serialVersionUID = 1L;
    protected Set<E> delegate;

    protected ComponentSetImpl(Set<E> delegate) {
        this.delegate = delegate;
    }

    private O owner;

    @Override
    public O getOwner() {
        return owner;
    }

    @Override
    public void setOwner(O owner) {
        this.owner = Components.checkSetOwner(this, this.owner, owner);
        for (E e : this) {
            e.setOwner(this.owner);
        }
    }

    @Override
    protected Set<E> delegate() {
        return delegate;
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
            Components.setOwner(element, getOwner());
        }
        return added;
    }

    @Override
    public boolean remove(Object object) {
        boolean removed = super.remove(object);
        if (removed) {
            Components.setOwner((ComponentWithOwnerReference) object, null);
        }
        return removed;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean added = super.addAll(collection);
        if (added) {
            for (E e : collection) {
                Components.setOwner(e, getOwner());
            }
        }
        return added;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return standardRetainAll(collection);
    }

    @Override
    public void clear() {
        for (E e : delegate) {
            Components.setOwner(e, null);
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
            Components.setOwner(current, null);
            super.remove();
        }
    }
}
