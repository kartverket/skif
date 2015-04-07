package no.statkart.skif.store;

import com.google.common.base.Preconditions;
import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Set;

/**
 * Implementasjon av {@link no.statkart.skif.store.InverseValueCollection} for wrapping av {@link java.util.Set} for bruk
 * i bobler.
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
public class InverseValueSet<O extends BubbleObject&InverseRelationParticipation, E > extends AbstractInverseValueSet<O,E> {
    private static final long serialVersionUID = 1L;
    private final O owner;

    public InverseValueSet(O owner, RelationName relationName, Set<E> delegate) {
        super(relationName, delegate);
        this.owner = Preconditions.checkNotNull(owner);
    }

    @Override
    public O getOwner() {
        return owner;
    }
}
