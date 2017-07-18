package no.statkart.skif.store;

import java.util.List;

/**
 * Implementasjon av {@link no.statkart.skif.store.ComponentCollection} for wrapping av {@link java.util.List} hvor
 * eiende objekt er en CompositeComponent.
 * @author Henrik Fredholm
 * @since 2.4.0
 */
public class CompositeComponentList<O, E extends ComponentWithOwnerReference<O>> extends AbstractComponentList<O, E> {
    private static final long serialVersionUID = 1L;
    private final CompositeComponent<O, ?> owner;

    public CompositeComponentList(CompositeComponent<O, ?> owner, List<E> delegate) {
        super(delegate);
        this.owner = owner;
    }

    @Override
    public O getOwner() {
        return owner.getCompositeRootOwner();
    }
}
