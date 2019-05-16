package no.statkart.skif.store;

import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingSet;
import no.statkart.skif.store.relation.cache.RelationName;

import java.util.*;

/**
 * Wrapper klasse for {@link Set} som inneholder objekter med boblereferanser som inngår i invers relasjoner. Slike
 * {@code Set} må overvåkes for ({@code add}- og {@code remove}-operasjoner for å kunne holde invers
 * relasjoner i {@link no.statkart.skif.store.relation.cache.RelationCache} oppdatert når innhold i settet endres.
 * <p/>
 * Bemerk: I definisjonen av denne klassen burde {@link #getOwner()} egentlig extende både {@code BubbleObject} og
 * {@code InverseRelationParticipation}, men det er ikke hensiktsmessig fordi da må man for owners at typen
 * {@link ComponentWithOwnerReference} eksplisitt angi den eidende bobleklassen i tillegg til den direkte eiende klassen
 * og det blir veldig tungvint. Har derfor istedet valgt å lage en package private hjelpemetode
 * {@link Bubbles#onChangeRelationImpl} som kun krever at owner som hentes ut er av type BubbleObject.
 * <p>
 * Eksempel på bruk:
 * <blockquote><pre>
 *     final Set<MyValueObject> myObjects = new AbstractInverseRelationTrackingSet<MyValueObject>(this, new HashSet()) {
 *         private static final long serialVersionUID = 1L;
 *         {@code@Override}
 *         protected Map<RelationName, Object> getInverseRelationValues(MyValueObject element) {
 *             return ImmutableMap.of(MyService.Role.valueobjectPointsToMe, e.getSomeId());
 *         }
 *     };
 * </pre></blockquote>
 *
 * @since 2.8.0
 */
public abstract class AbstractInverseRelationTrackingSet<E> extends ForwardingSet<E> implements InverseValueCollection<BubbleObject, E> {
    private static final long serialVersionUID = 1L;
    protected Set<E> delegate;
    private final OwningBubbleExtractor ownerExtractor;
    public <O extends BubbleObject & InverseRelationParticipation> AbstractInverseRelationTrackingSet(O owner, Set<E> delegate) {
        Objects.requireNonNull(owner);
        this.delegate = delegate;
        this.ownerExtractor = OwningBubbleExtractor.create(owner);
    }

    /* Kommentert ut da den forvirrer IntelliJ IDEA og gir sporadiske kompileringsfeil
    public <O extends ComponentWithOwnerReference<?> & InverseRelationParticipation> AbstractInverseRelationTrackingSet(O owner, Set<E> delegate) {
        Preconditions.checkNotNull(owner);
        this.delegate = delegate;
        this.ownerExtractor = OwningBubbleExtractor.create(owner);
    }
    */

    public BubbleObject getOwner() {
        return ownerExtractor.getOwner();
    }

    protected abstract Map<RelationName, Object> getInverseRelationValues(E element);

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
            BubbleObject owner = getOwner();
            for (Map.Entry<RelationName, Object> entry : getInverseRelationValues(element).entrySet()) {
                Bubbles.onChangeRelationImpl(owner, entry.getKey(), null, entry.getValue());
            }
        }
        return added;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object object) {
        boolean removed = super.remove(object);
        if (removed) {
            BubbleObject owner = getOwner();
            for (Map.Entry<RelationName, Object> entry : getInverseRelationValues((E)object).entrySet()) {
                Bubbles.onChangeRelationImpl(owner, entry.getKey(), entry.getValue(), null);
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
        BubbleObject owner = getOwner();
        for (E e : delegate) {
            for (Map.Entry<RelationName, Object> entry : getInverseRelationValues(e).entrySet()) {
                Bubbles.onChangeRelationImpl(owner, entry.getKey(), entry.getValue(), null);
            }
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
            BubbleObject owner = getOwner();
            for (Map.Entry<RelationName, Object> entry : getInverseRelationValues(current).entrySet()) {
                Bubbles.onChangeRelationImpl(owner, entry.getKey(), entry.getValue(), null);
            }
            super.remove();
        }
    }
}
