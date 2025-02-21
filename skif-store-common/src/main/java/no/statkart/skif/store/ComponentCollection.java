package no.statkart.skif.store;

import java.io.Serializable;
import java.util.Collection;

/**
 * Interface for Collection av components, som automatisk setter owner på components.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public interface ComponentCollection<O, E extends ComponentWithOwnerReference<O>> extends Collection<E>, Serializable {
}
