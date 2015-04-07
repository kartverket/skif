package no.statkart.skif.store;

import com.google.common.base.Preconditions;
import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Set;

/**
 * Implementasjon av {@link no.statkart.skif.store.InverseValueCollection} for wrapping av {@link java.util.Set} for bruk i komponenter.
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
public class ComponentInverseValueSet<O extends ComponentWithOwnerReference<?>&InverseRelationParticipation, E> extends AbstractInverseValueSet<BubbleObject,E> {
    private static final long serialVersionUID = 1L;
    private final O owner;

    public ComponentInverseValueSet(O owner, RelationName relationName, Set<E> delegate) {
        super(relationName, delegate);
        this.owner = Preconditions.checkNotNull(owner);
    }

    @Override
    public BubbleObject getOwner() {
        return Components.getOwningBubble(owner);
    }
}
