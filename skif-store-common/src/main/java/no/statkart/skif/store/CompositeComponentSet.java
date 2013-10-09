package no.statkart.skif.store;

import java.util.Set;

/**
 * Implementasjon av {@link no.statkart.skif.store.ComponentCollection} for wrapping av {@link java.util.Set} hvor
 * eiende objekt er en CompositeComponent.
 * i
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
public class CompositeComponentSet<O, E extends ComponentWithOwnerReference<O>> extends AbstractComponentSet<O,E> {
    private static final long serialVersionUID = 1L;
    private final CompositeComponent<O,?> owner;

    public CompositeComponentSet(CompositeComponent<O,?> owner, Set<E> delegate) {
        super(delegate);
        this.owner = owner;
    }

    @Override
    public O getOwner() {
        return owner.getCompositeRootOwner();
    }
}
