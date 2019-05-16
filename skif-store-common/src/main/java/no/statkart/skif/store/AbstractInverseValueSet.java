package no.statkart.skif.store;

import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingSet;
import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Abstract implementasjon av {@link no.statkart.skif.store.InverseValueCollection} for wrapping av {@link java.util.Set}
 * av inverse values.
 * <p/>
 * Bemerk: I definisjonen av denne klassen burde {@code <O>} egentlig extende både {@code BubbleObject} og
 * {@code InverseRelationParticipation}, men det er ikke hensiktsmessig fordi da må subklassen {@link no.statkart.skif.store.ComponentInverseValueSet}
 * eksplisitt angi den eidende bobleklassen i tillegg til den direkte eiende klassen og det blir og det blir veldig
 * tungvint. Har derfor istedet valgt å lage en package private hjelpemetode {@link no.statkart.skif.store.Bubbles#onChangeRelationImpl}
 * som kun krever at {@code owner} er av type BubbleObject.
 *
 * @author Henrik Fredholm
 * @since 2.6.0
 */
public abstract class AbstractInverseValueSet<O extends BubbleObject, E> extends ForwardingSet<E> implements InverseValueCollection<O, E> {
    private static final long serialVersionUID = 1L;
    protected final RelationName relationName;
    protected Set<E> delegate;


    protected AbstractInverseValueSet(RelationName relationName, Set<E> delegate) {
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
            Bubbles.onChangeRelationImpl(owner, relationName, null, element);
        }
        return added;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object object) {
        boolean removed = super.remove(object);
        if (removed) {
            O owner = getOwner();
            Bubbles.onChangeRelationImpl(owner, relationName, object, null);
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
        for (E e : delegate) {
            Bubbles.onChangeRelationImpl(owner, relationName, e, null);
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
            Bubbles.onChangeRelationImpl(owner, relationName, current, null);
            super.remove();
        }
    }
}
