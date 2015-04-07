package no.statkart.skif.store;

import com.google.common.base.Preconditions;
import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Set;

/**
 * Implementasjon av {@link BubbleIdCollection} for wrapping av {@link java.util.Set} for bruk i komponenter.
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
@Deprecated
public class ComponentBubbleIdSet<O extends ComponentWithOwnerReference<?>&InverseRelationParticipation, E extends BubbleId<?>> extends AbstractBubbleIdIdSet<BubbleObject,E> {
    private static final long serialVersionUID = 1L;
    private final O owner;

    public ComponentBubbleIdSet(O owner, RelationName relationName, Set<E> delegate) {
        super(relationName, delegate);
        this.owner = Preconditions.checkNotNull(owner);
    }

    @Override
    public BubbleObject getOwner() {
        return Components.getOwningBubble(owner);
    }
}
