package no.statkart.skif.store;

import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementasjon av {@link ComponentCollection} for wrapping av {@link Set}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class ComponentSet<O, E extends ComponentWithOwnerReference<O>> extends ForwardingSet<E> implements ComponentCollection<O, E> {
    private final O owner;
    private final Set<E> delegate;

    public ComponentSet(O owner, Set<E> delegate) {
        this.owner = owner;
        this.delegate = delegate;
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
            Components.setOwner(element, owner);
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
                Components.setOwner(e, owner);
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
