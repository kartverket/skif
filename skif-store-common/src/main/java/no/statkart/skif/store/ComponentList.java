package no.statkart.skif.store;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingListIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Implementasjon av {@link ComponentCollection} for wrapping av {@link List}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class ComponentList<O, E extends ComponentWithOwnerReference<O>> extends AbstractComponentList<O, E> {
    private static final long serialVersionUID = 1L;
    private final O owner;

    public ComponentList(O owner, List<E> delegate) {
        super(delegate);
        this.owner = owner;
    }

    @Override
    public O getOwner() {
        return owner;
    }
}
