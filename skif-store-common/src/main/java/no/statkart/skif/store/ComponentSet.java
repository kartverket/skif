package no.statkart.skif.store;

import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementasjon av {@link ComponentCollection} for wrapping av {@link Set}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class ComponentSet<O, E extends ComponentWithOwnerReference<O>> extends AbstractComponentSet<O,E> {
    private static final long serialVersionUID = 1L;
    private final O owner;

    public ComponentSet(O owner, Set<E> delegate) {
        super(delegate);
        this.owner = owner;

    }

    @Override
    public O getOwner() {
        return owner;
    }
}
