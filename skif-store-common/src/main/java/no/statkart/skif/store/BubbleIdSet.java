package no.statkart.skif.store;

import com.google.common.base.Preconditions;
import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Set;

/**
 * Implementasjon av {@link no.statkart.skif.store.BubbleIdCollection} for wrapping av {@link java.util.Set} for bruk
 * i bobler.
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
@Deprecated
public class BubbleIdSet<O extends BubbleObject&InverseRelationParticipation, E extends BubbleId<?>> extends AbstractBubbleIdIdSet<O,E> {
    private static final long serialVersionUID = 1L;
    private final O owner;

    public BubbleIdSet(O owner, RelationName relationName, Set<E> delegate) {
        super(relationName, delegate);
        this.owner = Preconditions.checkNotNull(owner);

    }

    @Override
    public O getOwner() {
        return owner;
    }
}
