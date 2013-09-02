package no.statkart.skif.store;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingListIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Implementasjon av {@link ComponentCollection} for wrapping av {@link List}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class ComponentList<O, E extends ComponentWithOwnerReference<O>> extends ForwardingList<E> implements ComponentCollection<O, E> {
    private final O owner;
    private final List<E> delegate;

    public ComponentList(O owner, List<E> delegate) {
        this.owner = owner;
        this.delegate = delegate;
    }

    @Override
    protected List<E> delegate() {
        return delegate;
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        Components.setOwner(element, owner);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> elements) {
        boolean added = super.addAll(index, elements);
        if (added) {
            for (E e : elements) {
                Components.setOwner(e, owner);
            }
        }
        return added;
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new IteratorWrapper(super.listIterator(index));
    }

    @Override
    public E remove(int index) {
        E removed = super.remove(index);
        if (removed != null) {
            Components.setOwner(removed, null);
        }
        return removed;
    }

    @Override
    public E set(int index, E element) {
        E old = super.set(index, element);
        Components.setOwner(old, null);
        Components.setOwner(element, owner);
        return old;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
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

    private class IteratorWrapper extends ForwardingListIterator<E> {
        private final ListIterator<E> delegate;
        private E current;

        private IteratorWrapper(ListIterator<E> delegate) {
            this.delegate = delegate;
        }

        @Override
        protected ListIterator<E> delegate() {
            return delegate;
        }

        @Override
        public void set(E element) {
            super.set(element);
            Components.setOwner(current, null);
            Components.setOwner(element, owner);
            current = element;
        }

        @Override
        public E previous() {
            current = super.previous();
            return current;
        }

        @Override
        public E next() {
            current = super.next();
            return current;
        }

        @Override
        public void remove() {
            super.remove();
            Components.setOwner(current, null);
        }

        @Override
        public void add(E element) {
            super.add(element);
            Components.setOwner(element, owner);
        }
    }
}
