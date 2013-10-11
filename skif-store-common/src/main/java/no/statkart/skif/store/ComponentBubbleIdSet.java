package no.statkart.skif.store;

import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Set;

/**
 * Implementasjon av {@link BubbleIdCollection} for wrapping av {@link java.util.Set} for bruk i komponenter.
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
public class ComponentBubbleIdSet<O extends ComponentWithOwnerReference<?>, E extends BubbleId<?>> extends AbstractBubbleIdIdSet<AbstractBubbleObject,E> {
    private static final long serialVersionUID = 1L;
    private final O owner;

    public ComponentBubbleIdSet(O owner, RelationName relationName, Set<E> delegate) {
        super(relationName, delegate);
        this.owner = owner;
    }

    @Override
    public AbstractBubbleObject getOwner() {
        return Components.getOwningBubble(owner);
    }
}
